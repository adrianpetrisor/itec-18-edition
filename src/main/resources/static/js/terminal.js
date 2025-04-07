$(document).ready(function () {
    const terminalOutput = document.getElementById("output");
    const commandInput = $("#command-input");
    const terminal = $(".terminal");

    let userScrolled = false;
    const observer = new MutationObserver(() => {
        if (!userScrolled) {
            terminalOutput.scrollTop = terminalOutput.scrollHeight;
        }
    });

    observer.observe(terminalOutput, { childList: true });

    terminalOutput.addEventListener("scroll", function () {
        userScrolled = terminalOutput.scrollHeight - terminalOutput.scrollTop > terminalOutput.clientHeight + 10;
    });

    terminal.on("click", function () {
        commandInput.focus();
    });

    commandInput.keypress(function (e) {
        if (e.which === 13) {
            e.preventDefault();
            let command = $(this).text().trim();
            $(this).text("");

            if (command !== "") {
                $("#output").append(`<div><span class="prompt">user@root:~$</span> ${command}</div>`);
                handleCommand(command);
            }
        }
    });

    function handleCommand(command) {
        let response = "";

        switch (command.toLowerCase()) {
            case "help":
                response = "<strong>Available commands:</strong><br>" +
                    "- help → Displays this list<br>" +
                    "- clear → Clears the terminal<br>" +
                    "- about → Info about the terminal<br>" +
                    "- date → Shows the current date and time<br>" +
                    "- tehnologies → Technologies used<br>" +
                    "- about us → Info about the developers";
                break;
            case "clear":
                $("#output").html("");
                return;
            case "about":
                response = "Web Terminal v1.0";
                break;
            case "date":
                response = new Date().toLocaleString();
                break;
            case "tehnologies":
                response = "Platform built with:<br>- Back-End: Spring Boot<br>- Front-End: Thymeleaf";
                break;
            case "about us":
                response = "<strong>Adrian</strong> – Front-End Developer, Year 1, UPB<br>" +
                    "<strong>Alexandru</strong> – Senior Java Developer, Year 1, UPT";
                break;
            default:
                response = `Unknown command: <strong>${command}</strong>. Type <strong>help</strong> for assistance.`;
        }



        $("#output").append(`<div>${response}</div>`);
    }
});