function _(str, locale) {
    locale = locale || _.defaultLocale;
    if (_.data.hasOwnProperty(locale) && typeof _.data[locale] == 'object') {
        if (_.data[locale].hasOwnProperty(str)) {
            return _.data[locale][str];
        }
    }
    return str;
}

_.defaultLocale = 'ru';
_.data = {
    ru: {}
};
_.registerLocale = function registerLocale(locale, data) {
    if (!_.data.hasOwnProperty(locale)) {
        _.data[locale] = {};
    }
    for (let str in data) {
        if (data.hasOwnProperty(str)) {
            _.data[locale][str] = data[str];
        }
    }
}

_.registerLocale('ru', {
    'pattern_detail': 'Некорректный ввод. Пример: -1,2',
    'required': ' Поле обязательно для заполнения',
    'not_in_range': ' Число не находится в пределах от -3 до 3',
    'hit_true': 'Попадание',
    'hit_false': 'Промах',
    'results': 'Результат',
});