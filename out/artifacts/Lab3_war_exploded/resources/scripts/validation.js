function setError(text, elem, errorSpanId) {
    let button = document.getElementById("button-submit");
    let error = document.getElementById(errorSpanId);
    error.classList.add("error");

    button.disabled = true;
    error.innerHTML = text;
    elem.classList.add("error");
}

function unsetError(elem, errorSpanId) {
    let button = document.getElementById("button-submit");
    let error = document.getElementById(errorSpanId);

    button.disabled = false;
    error.innerHTML = "";
    elem.classList.remove("error");
}

// Для переключения кнопок c именем buttonsName и элементом hiddenId
function onclickButton(event, buttonsClass, hiddenId) {
    let selector;
    if (!buttonsClass.startsWith("."))
        selector = ".";
    selector += buttonsClass;
    let elements = document.querySelectorAll(selector);
    for (let i = 0; i < elements.length; i++) {
        if (elements[i].classList.contains("pushed"))
            elements[i].classList.remove("pushed");
    }

    let current = event.currentTarget;
    current.classList.add("pushed");
    let hidden = document.getElementById(hiddenId);
    hidden.value = current.value;
}

function onclickCheckbox(event, boxesClass, hiddenId) {
    let selector = "";
    if (!boxesClass.startsWith("."))
        selector = ".";
    selector += boxesClass;
    let checkboxes = document.querySelectorAll(selector);
    for (let i = 0; i < checkboxes.length; i++) {
        // Сброс всех чекбоксов
        checkboxes[i].checked = false;
    }
    // Выбор текущего
    event.currentTarget.checked = true;
    let hidden = document.getElementById(hiddenId);
    hidden.value = event.currentTarget.title;
    // Сигнал о смене параметра для того, чтобы обновить график
    let ev = new Event("changeValue");
    hidden.dispatchEvent(ev);
}

// Для перехвата события отправки формы, чтобы проверить данные
function beforeSubmit(event) {
    let y = document.getElementById("input-Y");
    if (!y.oninput(null)) {
        if (event.preventDefault) event.preventDefault();
        if (event.returnValue) event.returnValue = false;
        return false;
    }
    let ev = new Event("afterClick");
    document.getElementById("button-submit").dispatchEvent(ev);
    return true;
}

// Для вывода сообщений об ошибках при отладке
window.onerror = function (msg, url, line) {
    alert("Ошибка: " + msg + "\n" + url + ":" + line);
    return true;
};
window.addEventListener("load", function() {
    let y = document.getElementById("input-Y");
    // Проверка ввода для Y
    y.oninput = function () {
        let reg = /^[+-]?\d+[.,]?\d*$/
        if (!reg.test(this.value)) {
            if (this.value === "") {
                setError(_("required", "ru"), this, "error-Y");
            } else
                setError(_("pattern_detail", "ru"), this, "error-Y");
            return false;
        } else {
            let value = parseFloat(this.value.replace(",", "."));
            if (value < -3.0 || value > 3.0) {
                setError(_("not_in_range", "ru"), this, "error-Y");
                return false;
            } else {
                unsetError(this, "error-Y");
            }
        }
        // Координата Y отправляется из поля _input-Y
        let _y = document.getElementById("_input-Y");
        _y.value = y.value;
        return true;
    }

    // Для каждой кнопки button-R, добавление функции на нажатие
    let buttonsR = document.querySelectorAll(".button-R");
    for (let i = 0; i < buttonsR.length; i++) {
        buttonsR[i].onchange = function (event) {
            onclickCheckbox(event, ".button-R", "input-R");
        };
    }
    // Выбор параметра R по умолчанию
    let event = new Event("change");
    buttonsR[0].dispatchEvent(event);

    // Для каждой кнопки button-X, добавление функции на клик
    let buttonsX = document.querySelectorAll(".button-X");
    for (let i = 0; i < buttonsX.length; i++) {
        buttonsX[i].onclick = function (event) {
            onclickButton(event, "button-X", "input-X");
        }
    }
    // Выбор первой кнопки по умолчанию
    buttonsX[0].click();

    // Добавление отправки формы и переключение фокуса при нажатии на Enter
    // для всех элементов формы
    let choose = document.querySelectorAll(".choose");
    for (let i = 0; i < choose.length; i++) {
        choose[i].addEventListener("keydown", function (event) {
            if (event.key === "Enter") {
                if (beforeSubmit(event)) {
                    this.blur();
                    let ev = new Event("click");
                    document.getElementById("button-submit").dispatchEvent(ev);
                }
            }
        }, true);
    }
}, false);
