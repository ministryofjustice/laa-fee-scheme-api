import {
  Before,
  BeforeAll,
  ITestCaseHookParameter,
  setDefaultTimeout,
} from '@cucumber/cucumber';
import dotenv from 'dotenv';
import * as fs from 'fs';
import * as path from 'path';
import World from './world';

setDefaultTimeout(180 * 1000);
dotenv.config();

BeforeAll(function () {
  const attachmentsDir = path.join(process.cwd(), 'reports', 'attachments');
  fs.rmSync(attachmentsDir, { recursive: true, force: true });
  fs.mkdirSync(attachmentsDir, { recursive: true });
});

Before(function (this: World, scenario: ITestCaseHookParameter) {
  this.currentScenarioName = scenario.pickle.name || 'UnnamedScenario';
  this.tags = scenario.pickle.tags.map((tag) => tag.name);
});
