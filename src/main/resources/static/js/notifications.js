const duration = 10000;
const maxNotifications = 3;
const notificationSpacing = 15;

function showNotification(type, message) {
    const templates = {
        success: "#succes-notification",
        fail: "#fail-notification",
        warning: "#warning-notification"
    };

    const $template = $(templates[type]).clone().removeAttr("id").addClass("stacked");

    $template.find(".sub-text").text(message);
    $("body").append($template);
    updateStack();

    $template.css({ right: "-400px", display: "flex" })
        .animate({ right: "20px" }, 800);

    startProgressBar(duration, $template);
    setTimeout(() => hideNotification($template), duration);
}

function hideNotification(notification) {
    notification.animate({ right: "-400px" }, 800, function () {
        $(this).remove();
        updateStack();
    });
}

function startProgressBar(time, notification) {
    notification.find(".progress-bar").css({
        width: "100%",
        transition: `width ${time / 1000}s linear`
    }).width("0%");
}

function updateStack() {
    let notifications = $(".stacked");
    if (notifications.length > maxNotifications) {
        notifications.first().remove();
    }
    notifications.each(function (index) {
        $(this).css("top", `${10 + (index * (70 + notificationSpacing))}px`);
    });
}

$(document).ready(function () {
    $(document).on("click", ".cross-icon", function () {
        let notification = $(this).closest(".notification");
        hideNotification(notification);
    });
});
