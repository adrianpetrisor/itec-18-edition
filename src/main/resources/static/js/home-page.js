function showContent(selectedFeature) {
    document.querySelectorAll('.feature').forEach(feature => {
        const desc = feature.querySelector('.description');

        anime.remove(desc);

        desc.style.display = 'none';
        desc.style.opacity = 0;

        feature.classList.remove('active');
    });

    selectedFeature.classList.add('active');
    const selectedDesc = selectedFeature.querySelector('.description');
    selectedDesc.style.display = 'block';

    anime({
        targets: selectedDesc,
        opacity: [0, 1],
        duration: 500,
        easing: 'easeOutQuad'
    });

    anime({
        targets: selectedFeature.querySelector('.circle'),
        scale: [1, 1.3, 1],
        duration: 500,
        easing: 'easeInOutQuad'
    });
}

document.getElementById("navigation-button-features").addEventListener("click", function () {
    document.getElementById("features").scrollIntoView({ behavior: "smooth" });
});

document.getElementById("navigation-button-terminal").addEventListener("click", function () {
    document.getElementById("terminal-container").scrollIntoView({ behavior: "smooth" });
});

document.getElementById("navigation-button-contact").addEventListener("click", function () {
    document.getElementById("footer").scrollIntoView({ behavior: "smooth" });
});

document.getElementById("footer-home-button").addEventListener("click", function () {
    document.getElementById("home").scrollIntoView({ behavior: "smooth" });
});

document.getElementById("footer-about-button").addEventListener("click", function () {
    document.getElementById("terminal-container").scrollIntoView({ behavior: "smooth" });
});

function openWebsite() {
    window.open("https://www.itec.ro", "_blank");
}

function openInstagram() {
    window.open("https://www.instagram.com/itec.ligaac", "_blank");
}

function openFacebook() {
    window.open("https://www.facebook.com/it.engineering.contest/", "_blank");
}
