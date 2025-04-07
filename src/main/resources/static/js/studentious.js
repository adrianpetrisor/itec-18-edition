$(document).ready(function () {
    const $preview = $('#userPreview');
    const $name = $('#previewName');
    const $role = $('#previewRole');
    const $avatar = $('#previewAvatar');
    const $university = $('#previewUniversity');
    const $faculty = $('#previewFaculty');
    const $bio = $('#previewBio');

    let lastClicked = null;

    $('.user-card').on('click', function (e) {
        e.stopPropagation();

        const name = $(this).find('span').text();
        const role = $(this).data('role');
        const avatar = $(this).find('img').attr('src');
        const university = $(this).data('university');
        const faculty = $(this).data('faculty');
        const description = $(this).data('description');

        const rect = this.getBoundingClientRect();
        const isMobile = window.innerWidth <= 768;

        if (lastClicked === this && $preview.is(':visible')) {
            $preview.fadeOut(150);
            lastClicked = null;
            return;
        }

        lastClicked = this;

        $name.text(name);
        $role.text(role.toString().charAt(0).toUpperCase() + role.toString().slice(1));
        $avatar.attr('src', avatar);

        if(university != null && university.trim().length > 0) {
            $university.text('University: ' + university);
        }else {
            $university.text('University: Not specified.');
        }

        if(faculty != null && faculty.trim().length > 0) {
            $faculty.text('Faculty: ' + faculty);
        }else {
            $faculty.text('Faculty: Not specified.');
        }

        if(description != null && description.trim().length > 0) {
            $bio.text('Description: ' + description);
        }else {
            $bio.text('Description: Not specified.');

        }


        if (isMobile) {
            $preview
                .css({ top: 'auto', left: '0', right: '0' })
                .fadeIn(200);
        } else {
            $preview
                .css({
                    top: rect.top + window.scrollY + 'px',
                    left: rect.left - 350 + 'px',
                })
                .fadeIn(200);
        }
    });

    $(document).on('click', function (e) {
        if (!$(e.target).closest('.user-card').length && !$(e.target).closest('#userPreview').length) {
            $preview.fadeOut(150);
            lastClicked = null;
        }
    });
});

$(document).ready(function () {
    let isVisible = true;

    $('.toggle-users').on('click', function () {
        const $panel = $('.registered-event-users');

        if (isVisible) {
            $panel.animate({ right: '-300px', opacity: 0 }, 300, function () {
                $panel.css('display', 'none');
            });
        } else {
            $panel.css({ display: 'block', right: '-300px', opacity: 0 }).animate({ right: '0', opacity: 1 }, 300);
        }

        isVisible = !isVisible;
    });
});

$(document).ready(function () {
    const $burger = $('#burgerToggle');
    const $menu = $('.event-list');
    const $overlay = $('#menuOverlay');

    $burger.on('click', function () {
        $menu.toggleClass('active');
        $overlay.toggleClass('active');
    });

    $overlay.on('click', function () {
        $menu.removeClass('active');
        $overlay.removeClass('active');
    });
});

$(document).ready(function () {
    const $overlayImage = $('#dropOverlay');
    const $overlayFile = $('#fileDropOverlay');
    const $fileInputImage = $('#fileInputImages');
    const $fileInputGeneric = $('#fileInputGeneric');

    $('button[title="Photos"]').on('click', function () {
        $overlayImage.addClass('active');
        $fileInputImage.trigger('click');
    });

    $('button[title="Files"]').on('click', function () {
        $overlayFile.addClass('active');
        $fileInputGeneric.trigger('click');
    });


    $('.drop-overlay').on('click', function () {
        $(this).removeClass('active');
    });
});