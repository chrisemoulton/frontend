'use strict';
module.exports = function () {
    return {
        options: {
            rulePaths: ['./dev/eslint-rules']
        },
        'Gruntfile.js': [
            'Gruntfile.js'
        ],
        'grunt-configs': [
            'grunt-configs/**/*.js'
        ],
        'static/test/javascripts': {
            files: [{
                expand: true,
                cwd: 'static/test/javascripts',
                src: ['**/*.js']
            }],
            options: {
                // https://github.com/eslint/eslint/issues/2824
                ignorePath: 'static/test/javascripts/.eslintignore',
                quiet: true
            }
        },
        'static/src': {
            files: [{
                expand: true,
                cwd: 'static/src',
                src: ['**/*.js']
            }],
            options: {
                // https://github.com/eslint/eslint/issues/2824
                ignorePath: 'static/src/.eslintignore',
                quiet: true
            }
        }
    };
};
