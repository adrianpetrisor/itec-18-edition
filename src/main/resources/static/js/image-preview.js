document.addEventListener("DOMContentLoaded", () => {
    const avatarInput = document.getElementById("avatarInput");
    const avatarPreview = document.getElementById("avatarPreview");

    avatarInput.addEventListener("change", () => {
        const file = avatarInput.files[0];

        if (file && file.type.startsWith("image/")) {
            const reader = new FileReader();

            reader.onload = (e) => {
                avatarPreview.src = e.target.result;
            };

            reader.readAsDataURL(file);
        }
    });
});

document.addEventListener("DOMContentLoaded", function () {
    const select = document.getElementById("interest-select");
    const preview = document.getElementById("interest-live-preview");
    const saveBtn = document.getElementById("saveInterestsBtn");

    const selectedInterests = new Set();

    select.addEventListener("change", () => {
        const value = select.value;
        if (value && !selectedInterests.has(value)) {
            selectedInterests.add(value);

            const tag = document.createElement("span");
            tag.classList.add("interest-tag");
            tag.textContent = value;
            preview.appendChild(tag);
        }
        select.value = "";
    });

    saveBtn.addEventListener("click", () => {
        const interestsArray = Array.from(selectedInterests);
        console.log("Saved interests:", interestsArray);

    });
});
