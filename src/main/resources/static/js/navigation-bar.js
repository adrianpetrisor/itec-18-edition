$(document).ready(function () {
    let lastHeight = $(window).height();

    function checkWindowSize() {
        if ($(window).width() <= 1180) {
            $(".center-column").hide();
        } else {
            $(".center-column").show();
            $(".menu-icon__cheeckbox").prop("checked", false);
        }
    }

    checkWindowSize();

    $(".menu-icon__cheeckbox").change(function () {
        if ($(this).is(":checked")) {
            $(".center-column").slideDown(300);
        } else {
            $(".center-column").slideUp(300);
        }
    });

    $(window).resize(function () {
        let newHeight = $(window).height();
        if (newHeight !== lastHeight) {
            $(".menu-icon__cheeckbox").prop("checked", false);
        }
        lastHeight = newHeight;
        checkWindowSize();
    });
});