const isInquestFeatureEnabled = process.env.IS_INQUEST_FEATURE_ENABLED !== "false";

const tagExpression = isInquestFeatureEnabled ? "@api" : "@api and not @inquest";

module.exports = {
  api: [
    "--require-module ts-node/register",
    "--require features/**/*.ts",
    "--format json:reports/cucumber.json",
    "--format html:reports/cucumber.html",
    `--tags "${tagExpression}"`,
    "features/api/**/*.feature"
  ].join(" "),
};
