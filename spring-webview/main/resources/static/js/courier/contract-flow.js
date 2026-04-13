(function () {
    const overlay = document.getElementById("contractFlow");
    const openButton = document.getElementById("openContractFlow");
    const closeButtons = [document.getElementById("closeContractFlow"), document.getElementById("closeContractFlowInline")].filter(Boolean);
    const form = document.getElementById("contractFlowForm");
    const saveButton = document.getElementById("saveContractDraft");
    const generateButton = document.getElementById("generateContractDraft");
    const statusEl = document.getElementById("contractFlowStatus");
    const previewEl = document.getElementById("generatedContractPreview");
    const googleLink = document.getElementById("continueWithGoogle");
    const localSessionKey = document.getElementById("localSessionKey");
    const localContactEmail = document.getElementById("localContactEmail");
    const localContactName = document.getElementById("localContactName");
    const sessionStorageKey = "tavall:couriers:contract-session";
    const draftStorageKey = "tavall:couriers:contract-fields";
    if (!overlay || !form || !saveButton || !generateButton || !statusEl || !previewEl) {
        return;
    }

    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute("content") || "";
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute("content") || "";
    const getSessionKey = () => {
        let sessionKey = window.sessionStorage.getItem(sessionStorageKey);
        if (!sessionKey) {
            sessionKey = crypto.randomUUID();
            window.sessionStorage.setItem(sessionStorageKey, sessionKey);
        }
        return sessionKey;
    };

    const setStatus = (message) => {
        statusEl.textContent = message;
    };

    const setOpen = (open) => {
        overlay.classList.toggle("open", open);
        overlay.setAttribute("aria-hidden", open ? "false" : "true");
        document.body.style.overflow = open ? "hidden" : "";
    };

    const readFields = () => {
        const values = {};
        new FormData(form).forEach((value, key) => {
            if (typeof value === "string" && value.trim()) {
                values[key] = value.trim();
            }
        });
        return values;
    };

    const hydrateFields = () => {
        const raw = window.sessionStorage.getItem(draftStorageKey);
        if (!raw) {
            return;
        }
        try {
            const values = JSON.parse(raw);
            Object.entries(values).forEach(([key, value]) => {
                const field = form.elements.namedItem(key);
                if (field && "value" in field && typeof value === "string") {
                    field.value = value;
                }
            });
        } catch {
            // Ignore malformed session state.
        }
    };

    const persistFields = () => {
        const fields = readFields();
        window.sessionStorage.setItem(draftStorageKey, JSON.stringify(fields));
        if (localSessionKey) {
            localSessionKey.value = getSessionKey();
        }
        if (localContactEmail) {
            localContactEmail.value = fields.contactEmail || "";
        }
        if (localContactName) {
            localContactName.value = fields.contactName || "";
        }
        if (googleLink && window.COURIER_CONTRACT_FLOW?.googleStartUrl) {
            googleLink.href = `${window.COURIER_CONTRACT_FLOW.googleStartUrl}?sessionKey=${encodeURIComponent(getSessionKey())}`;
        }
        return fields;
    };

    const postJson = async (url, payload) => {
        const headers = { "Content-Type": "application/json" };
        if (csrfToken && csrfHeader) {
            headers[csrfHeader] = csrfToken;
        }
        const response = await fetch(url, {
            method: "POST",
            headers,
            body: JSON.stringify({ sessionKey: getSessionKey(), ...payload })
        });
        if (!response.ok) {
            const text = await response.text();
            throw new Error(text || "Request failed.");
        }
        return response.json();
    };

    openButton?.addEventListener("click", () => setOpen(true));
    closeButtons.forEach((button) => button.addEventListener("click", () => setOpen(false)));
    overlay.addEventListener("click", (event) => {
        if (event.target === overlay) {
            setOpen(false);
        }
    });
    form.addEventListener("change", persistFields);
    form.addEventListener("input", persistFields);

    saveButton.addEventListener("click", async () => {
        try {
            const fields = persistFields();
            await postJson("/api/client/contracts/draft", fields);
            setStatus("Progress saved to this browser session.");
        } catch (error) {
            setStatus(error instanceof Error ? error.message : "Unable to save draft.");
        }
    });

    generateButton.addEventListener("click", async () => {
        try {
            const fields = persistFields();
            const response = await postJson("/api/client/contracts/draft/generate", fields);
            previewEl.innerHTML = response.generatedContractHtml || "<p>No contract output returned.</p>";
            setStatus("Contract generated. Continue with Google or the local client test to save it.");
        } catch (error) {
            setStatus(error instanceof Error ? error.message : "Unable to generate contract.");
        }
    });

    hydrateFields();
    persistFields();
})();
