module.exports = function (config) {
    // jscs:disable requireCapitalizedConstructors
    var settings = new require('./settings.js')(config);
    // jscs:enable requireCapitalizedConstructors
    settings.files.push(
        { pattern: 'static/test/javascripts/spec/membership/**/*.spec.js', included: false }
    );
    config.set(settings);
};
