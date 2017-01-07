const gulp = require('gulp');
const tsc = require('gulp-typescript');
const clean = require('gulp-clean');

const src = {
    ts: ["./src/**/*.ts", "./typings/**/*.ts"],
    html: "./src/**/*.html"
};

const build = {
    dest: "./build",
    jsFiles: this.dest + "/**/*.js"
};

const compilation = {
    target: 'es6',
    module: 'commonjs',
    declaration: false,
    "sourceMap": true,
    "noImplicitAny": false,
    "removeComments": false,
    "preserveConstEnums": false,
    "experimentalDecorators": true
};


gulp.task('compile-ts', () => {
    return gulp.src(src.ts)
        .pipe(tsc(compilation))
        .pipe(gulp.dest(build.dest))
});

gulp.task("clean-js", () => {
    return gulp.src(build.jsFiles)
        .pipe(clean());
});

gulp.task("html", () => {
    return gulp.src(src.html)
        .pipe(gulp.dest(build.dest))
});


gulp.task('default', ['clean-js', 'compile-ts', 'html'], () => {
    gulp.watch(src.ts, ['clean-js', 'compile-ts']);
    gulp.watch(src.html, ['html']);
});

