module.exports = {
  api: [
    "--require-module ts-node/register",
    "--require features/**/*.ts",
    "--format json:reports/cucumber.json",
    "--format html:reports/cucumber.html",
    "--tags @api",
    "features/api/**/*.feature"
  ].join(" "),
};
