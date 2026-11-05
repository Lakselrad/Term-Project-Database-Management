// Small progressive enhancements. Every page still works without JavaScript.
document.addEventListener("DOMContentLoaded", () => {
    // Submit the parent form when a dropdown with data-autosubmit changes.
    document.querySelectorAll("select[data-autosubmit]").forEach((select) => {
        select.addEventListener("change", () => select.form.submit());
    });

    // Ask for confirmation before submitting forms with data-confirm.
    document.querySelectorAll("form[data-confirm]").forEach((form) => {
        form.addEventListener("submit", (event) => {
            if (!window.confirm(form.dataset.confirm)) {
                event.preventDefault();
            }
        });
    });

    // Dismissible flash messages.
    document.querySelectorAll("[data-flash-close]").forEach((button) => {
        button.addEventListener("click", () => button.closest("[data-flash]").remove());
    });

    // User dropdown in the navbar.
    document.querySelectorAll("[data-dropdown]").forEach((dropdown) => {
        dropdown.querySelector("[data-dropdown-toggle]").addEventListener("click", (event) => {
            event.stopPropagation();
            dropdown.classList.toggle("open");
        });
    });
    document.addEventListener("click", () => {
        document.querySelectorAll("[data-dropdown].open").forEach((d) => d.classList.remove("open"));
    });

    // Mobile menu toggle.
    const navToggle = document.querySelector("[data-nav-toggle]");
    if (navToggle) {
        navToggle.addEventListener("click", () => {
            document.querySelector("[data-nav-links]").classList.toggle("open");
        });
    }

    // Character counter for textareas with data-counter.
    document.querySelectorAll("textarea[data-counter]").forEach((textarea) => {
        const output = textarea.parentElement.querySelector("[data-counter-output]");
        const max = textarea.getAttribute("maxlength");
        const update = () => {
            output.textContent = `${textarea.value.length} / ${max}`;
        };
        textarea.addEventListener("input", update);
        update();
    });

    // Registration: warn when the two passwords do not match.
    const registerForm = document.querySelector("[data-register-form]");
    if (registerForm) {
        const password = registerForm.elements.password;
        const repeat = registerForm.elements.passwordRepeat;
        const warning = registerForm.querySelector("[data-password-mismatch]");
        const check = () => {
            const mismatch = repeat.value.length > 0 && password.value !== repeat.value;
            warning.hidden = !mismatch;
            repeat.setCustomValidity(mismatch ? "Passwords do not match." : "");
        };
        password.addEventListener("input", check);
        repeat.addEventListener("input", check);
    }
});
