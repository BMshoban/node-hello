import gulp from "gulp";
import clean from "gulp-clean";
import uglify from "gulp-uglify";
import zip from "gulp-zip";

// Define paths
const paths = {
  src: ["index.js"], // main entry
  dist: "dist",
};

// 🧹 Clean dist folder
export const cleanDist = () => {
  return gulp.src(paths.dist, { allowEmpty: true, read: false }).pipe(clean());
};

// 📦 Copy JS files to dist
export const copyFiles = () => {
  return gulp.src(paths.src).pipe(gulp.dest(paths.dist));
};

// 🔧 Minify JS files
export const minifyJS = () => {
  return gulp.src(`${paths.dist}/**/*.js`).pipe(uglify()).pipe(gulp.dest(paths.dist));
};

// 📁 Zip the dist folder
export const zipDist = () => {
  return gulp.src(`${paths.dist}/**/*`).pipe(zip("backend.zip")).pipe(gulp.dest("."));
};

// 🧠 Default Gulp task sequence
export const build = gulp.series(cleanDist, copyFiles, minifyJS, zipDist);

