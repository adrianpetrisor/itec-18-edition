document.addEventListener("DOMContentLoaded", function () {
    const input = document.getElementById("confirmationCode");

    input.addEventListener("keydown", function (e) {
        if (e.key.length === 1 && !/[0-9]/.test(e.key)) {
            e.preventDefault();
        }
    });
});
