const gulp = require('gulp');
const tsc = require('gulp-typescript');
const clean = require('gulp-clean');

var src = {
    ts: "./src/**/*.ts",
    html: "./src/**/*.html"
};

const build = {
    dest: "./build",
    jsFiles: this.dest + "/**/*.js"
}

const compilation = {
    target: 'es5',
    module: 'commonjs',
    declaration: false,
    "sourceMap": true,
    "noImplicitAny": false,
    "removeComments": false,
    "preserveConstEnums": false,
    "experimentalDecorators": true
};


gulp.task('compile', () => {
    return gulp.src(src.ts)
        .pipe(tsc(compilation))
        .pipe(gulp.dest(build.dest))
});

gulp.task("cleanJs", () => {
    return gulp.src(build.jsFiles)
        .pipe(clean());
});

gulp.task("html", () => {
    return gulp.src(src.html)
        .pipe(gulp.dest(build.dest))
});


gulp.task('default', ['cleanJs', 'compile', 'html'], () => {
    gulp.watch(src.ts, ['cleanJs', 'compile']);
    gulp.watch(src.html, ['html']);
});

