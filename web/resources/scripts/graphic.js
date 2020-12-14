let canvas, c, R;
window.addEventListener("load", function() {
    R = document.getElementById("input-R");
    R.setAttribute("data-unit", "1");

    // Убирает график по-умолчанию в виде png
    let img = document.querySelector("#graphic img");
    img.classList.add("none");

    canvas = document.createElement("canvas");
    canvas.width = img.width;
    canvas.height = img.height;
    document.getElementById("graphic").append(canvas);
    c = canvas.getContext("2d");

    // Добавляет слушатель события нажатия на canvas, а именно: при нажатии
    // заполняется и отправляется форма
    canvas.onclick = function (event) {
        let unit = Number(R.dataset["unit"]);
        let x = getXinCanvas(event, canvas) - canvas.width / 2;
        let y = getYinCanvas(event, canvas) - canvas.height / 2;
        let r = R.value.replaceAll(",", ".");

        x = x / unit * r;
        y = y / unit * r;
        let X = document.getElementById("input-X");
        let Y = document.getElementById("_input-Y");
        let xOld = X.value;
        let yOld = Y.value;
        X.value = x;
        Y.value = -y;
        let ev = new Event("click");
        document.getElementById("button-submit").dispatchEvent(ev);
        X.value = xOld;
        Y.value = yOld;
    }

    // Отмена контекстного меню и двойного клика
    canvas.oncontextmenu = function (event) {
        return stopDefAction(event);
    };
    canvas.ondblclick = function(event) {
        return stopDefAction(event);
    }


    // При получении данного события (изменения значения параметра) выполнить
    // перерисовку графика
    document.getElementById("input-R").addEventListener("changeValue",
        updateGraphic);
    // При внесении изменений в таблицу выполнять перерисовку графика
    document.getElementById("results").addEventListener("DOMSubtreeModified",
        updateGraphic);

   updateGraphic();
}, false);

let updateGraphic = function() {
    drawPlate();
    drawOldPoints();
}

// Отвечает за прорисовку графика
let drawPlate = function () {
        c.restore();
        c.save();
        if (!R.value) return;

        // Белый фон
        c.fillStyle = "#fff";
        let width = canvas.width;
        let height = canvas.height;
        c.fillRect(0, 0, width, height);

        // Фигуры
        let offset = 10;
        c.translate(width / 2, height / 2);
        let xLen = width - 2 * offset - 4 * offset;
        let yLen = height - 2 * offset - 4 * offset;
        let unitR = div(xLen, 4) * 2;
        R.dataset["unit"] = (unitR).toString();
        c.miterLimit = 0;
        let figureColor = "#39F";
        // Ось X направлена вправо, а Y вниз!
        drawRect(c, -unitR, unitR/2, 0, 0, figureColor);
        drawTriangle(c, 0, unitR, unitR, 0, figureColor);
        drawCircle(c, 0, 0, unitR, 2, figureColor);

        // Координатная плоскость поверх всего, чтобы были видны оси и деления
        c.font = "14px monospaced";
        drawCoordinates(c, width, height, offset);
        let axisWidth = 1;
        drawDashes(c, xLen, div(xLen, 4), -1, 2,
            yLen, div(yLen, 4), -1, 2,
            2 + axisWidth);
    }
// Рисует все старые точки, данные берет из таблицы
let drawOldPoints = function () {
        let table = document.querySelector("#results-table");
        let Xi, Yi, hitI;
        if (!table || !table.rows[0] || !table.rows[0].cells) return;
        for (let cell of table.rows[0].cells) {
            if (cell.textContent.search(/\bX\b/) !== -1) Xi = cell.cellIndex;
            if (cell.textContent.search(/\bY\b/) !== -1) Yi = cell.cellIndex;
            if (cell.textContent.search(_("results", "ru")) !== -1) hitI = cell.cellIndex;
        }
        let tbody = table.tBodies[0];
        let unit = Number(document.getElementById("input-R").dataset["unit"]);
        let X, Y, hit;
        let r = R.value.replaceAll(",", ".");
        for (let i = 0; i < tbody.rows.length; i++) {
            let cells = tbody.rows[i].cells;
            X = Number(cells[Xi].textContent.replaceAll(",", "."));
            Y = Number(cells[Yi].textContent.replaceAll(",", "."));
            // R = Number(cells[Ri].textContent.replaceAll(",", "."));
            hit = cells[hitI].textContent.search(_("hit_true", "ru")) !== -1;

            let color = hit ? "#0f0" : "red";
            drawPoint(c, X / r * unit, -Y / r * unit, color);
        }
    }

function drawPoint(c, x, y, color) {
    c.save();
    c.beginPath();
    c.fillStyle = color || "red";
    c.moveTo(x, y);
    c.arc(x, y, 1.5, 0, 2 * Math.PI);
    c.fill();
    c.restore();
}

// Получение координат X, Y при нажатии на canvas (относительно canvas),
// используя событие нажатия event
function getXinCanvas(event, canvas) {
    if (!event || !canvas) return NaN;
    let bcr = canvas.getBoundingClientRect();
    return (event.clientX - bcr.left) * (canvas.width / bcr.width);
}
function getYinCanvas(event, canvas) {
    if (!event || !canvas) return NaN;
    let bcr = canvas.getBoundingClientRect();
    return (event.clientY - bcr.top) * (canvas.height / bcr.height);
}

// Отмена действий по умолчанию для какого-то события event
function stopDefAction(event) {
    if (event.preventDefault) event.preventDefault();
    if (event.returnValue) event.returnValue = false;
    return false;
}

function drawAxis(c, length, sign, rotated, lineWidth, color, tipLen, tipAngle) {
    lineWidth = lineWidth || 1;
    color = color || "#000";
    if (sign !== "" && !sign) {
        sign = "";
    }
    c.save();
    c.lineWidth = lineWidth;
    c.strokeStyle = color;
    c.beginPath();
    let x = 0;
    let y = 0;
    c.moveTo(x, y);
    x = length;
    y = 0;
    c.lineTo(x, y);
    tipLen = tipLen || 7;
    tipAngle = tipAngle || Math.PI / 6;
    x -= tipLen;
    y += Math.tan(tipAngle) * tipLen;
    c.lineTo(x, y);
    x += tipLen;
    y -= Math.tan(tipAngle) * tipLen;
    c.moveTo(x, y);
    x -= tipLen;
    y -= Math.tan(tipAngle) * tipLen;
    c.lineTo(x, y);
    c.stroke();

    c.translate(x, y);
    x = tipLen;
    y = 0;
    c.fillStyle = color || "#000";
    c.rotate(-rotated);
    c.fillText(sign, x, y);
    c.restore();
}

function drawDashes(c, xLen, xUnit, xMin, dx,
                    yLen, yUnit, yMin, dy,
                    dashLen, lineWidth, color) {
    c.save();
    dashLen = dashLen || 4;

    function drawDash(c, length) {
        c.moveTo(0, -length);
        c.lineTo(0, length);
        c.stroke();
    }

    lineWidth = lineWidth || 1;
    color = color || "#000";
    c.lineWidth = lineWidth;
    c.strokeStyle = color;
    c.fillStyle = color;

    c.save();
    c.beginPath();
    let left = xLen / 2;
    let dashCount = 0;
    while (left - xUnit > 0) {
        c.translate(-xUnit, 0);
        left -= xUnit;
        dashCount++;
    }
    dashCount = dashCount * 2 + 1; // одна дополнительная черта на оси координат
    let text;
    xMin *= dx;

    function getText(p) {
        let text;
        if (p === 0) {
            text = "";
        } else {
            text = round(p, 2);
        }
        /*} else if (p % dp === 0) {
            if (p / dp === 1) {
                text = "R";
            } else if (p / dp === -1) {
                text = "-R";
            } else
                text = (p / dp) + "R";
        } else {
            text = p + "/" + dp + "R";
        }*/
        return text;
    }

    let p = -Number(R.value.replaceAll(",", "."));
    let dp = round(Math.abs(p/2), 2);
    for (let i = 0; i <= dashCount; i++) {
        drawDash(c, dashLen);
        text = getText(p);
        c.fillText(text, -xUnit / 4, -xUnit / 4);
        p += dp;
        c.translate(xUnit, 0);
    }
    c.restore();

    left = yLen / 2;
    dashCount = 0;
    while (left - yUnit > 0) {
        c.translate(0, -yUnit);
        left -= xUnit;
        dashCount++;
    }
    c.rotate(Math.PI / 2);
    c.stroke();
    dashCount = dashCount * 2 + 1;
    p = Number(R.value.replaceAll(",", "."));
    for (let i = 0; i <= dashCount; i++) {
        drawDash(c, dashLen);
        text = getText(p);
        c.rotate(-Math.PI / 2);
        c.fillText(text, yUnit / 4, yUnit / 8);
        c.rotate(Math.PI / 2);
        p -= dp;
        c.translate(yUnit, 0);
    }
    c.restore();
}

function drawCoordinates(c, width, height, offset, axisWidth) {
    // Координатная плоскость
    c.save();
    c.setTransform(1, 0, 0, 1, 0, 0);
    offset = offset || 10;
    c.translate(offset, height / 2);
    axisWidth = axisWidth || 1;
    drawAxis(c, width - 2 * offset, "X", 0, axisWidth);
    c.translate(width / 2 - offset, height / 2 - offset);
    c.rotate(-Math.PI / 2);
    drawAxis(c, height - 2 * offset, "Y", -Math.PI / 2, axisWidth);
    c.restore();
}

function drawTriangle(c, x1, y1, x2, y2, color) {
    c.save();
    c.fillStyle = color || "#000";
    c.lineWidth = 1;
    c.beginPath();
    c.moveTo(0, 0);
    c.lineTo(x1, y1);
    c.lineTo(x2, y2);
    c.closePath();
    c.fill();
    c.restore();
}

function drawRect(c, x1, y1, x2, y2, color) {
    c.save();

    c.moveTo(x1, y1);
    c.beginPath();
    c.fillStyle = color || "#000";
    c.fillRect(x1, y1, x2 - x1, y2 - y1);

    c.restore();
}

function drawCircle(c, x, y, r, qr, color) {
    c.save();

    c.beginPath();
    c.moveTo(x, y);
    c.fillStyle = color || "#000";
    let startAngle = -Math.PI / 2 * (qr - 1);
    let endAngle = startAngle - Math.PI / 2;
    c.arc(x, y, r, startAngle, endAngle, true);
    c.fill();

    c.restore();
}

// Просто целочисленное деление..
function div(val, by) {
    return (val - val % by) / by;
}

function round(num, n) {
    let e = 1;
    for (let i = 0; i < n; i++) {
        e *= 10;
    }
    return Math.round(num * e) / e;
}