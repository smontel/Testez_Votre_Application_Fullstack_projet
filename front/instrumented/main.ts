function cov_1hjwqaca3o() {
  var path = "C:\\users\\niavl\\dev\\projet4\\Testez-une-application-full-stack\\front\\src\\main.ts";
  var hash = "ddda03b26a5144aaf8f22de7217a26b948585946";
  var global = new Function("return this")();
  var gcv = "__coverage__";
  var coverageData = {
    path: "C:\\users\\niavl\\dev\\projet4\\Testez-une-application-full-stack\\front\\src\\main.ts",
    statementMap: {
      "0": {
        start: {
          line: 7,
          column: 0
        },
        end: {
          line: 9,
          column: 1
        }
      },
      "1": {
        start: {
          line: 8,
          column: 2
        },
        end: {
          line: 8,
          column: 19
        }
      },
      "2": {
        start: {
          line: 11,
          column: 0
        },
        end: {
          line: 12,
          column: 36
        }
      },
      "3": {
        start: {
          line: 12,
          column: 16
        },
        end: {
          line: 12,
          column: 34
        }
      }
    },
    fnMap: {
      "0": {
        name: "(anonymous_0)",
        decl: {
          start: {
            line: 12,
            column: 9
          },
          end: {
            line: 12,
            column: 10
          }
        },
        loc: {
          start: {
            line: 12,
            column: 16
          },
          end: {
            line: 12,
            column: 34
          }
        },
        line: 12
      }
    },
    branchMap: {
      "0": {
        loc: {
          start: {
            line: 7,
            column: 0
          },
          end: {
            line: 9,
            column: 1
          }
        },
        type: "if",
        locations: [{
          start: {
            line: 7,
            column: 0
          },
          end: {
            line: 9,
            column: 1
          }
        }, {
          start: {
            line: 7,
            column: 0
          },
          end: {
            line: 9,
            column: 1
          }
        }],
        line: 7
      }
    },
    s: {
      "0": 0,
      "1": 0,
      "2": 0,
      "3": 0
    },
    f: {
      "0": 0
    },
    b: {
      "0": [0, 0]
    },
    _coverageSchema: "1a1c01bbd47fc00a2c39e90264f33305004495a9",
    hash: "ddda03b26a5144aaf8f22de7217a26b948585946"
  };
  var coverage = global[gcv] || (global[gcv] = {});
  if (!coverage[path] || coverage[path].hash !== hash) {
    coverage[path] = coverageData;
  }
  var actualCoverage = coverage[path];
  {
    // @ts-ignore
    cov_1hjwqaca3o = function () {
      return actualCoverage;
    };
  }
  return actualCoverage;
}
cov_1hjwqaca3o();
import { enableProdMode } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { AppModule } from './app/app.module';
import { environment } from './environments/environment';
cov_1hjwqaca3o().s[0]++;
if (environment.production) {
  cov_1hjwqaca3o().b[0][0]++;
  cov_1hjwqaca3o().s[1]++;
  enableProdMode();
} else {
  cov_1hjwqaca3o().b[0][1]++;
}
cov_1hjwqaca3o().s[2]++;
platformBrowserDynamic().bootstrapModule(AppModule).catch(err => {
  cov_1hjwqaca3o().f[0]++;
  cov_1hjwqaca3o().s[3]++;
  return console.error(err);
});
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJuYW1lcyI6WyJjb3ZfMWhqd3FhY2EzbyIsImFjdHVhbENvdmVyYWdlIiwiZW5hYmxlUHJvZE1vZGUiLCJwbGF0Zm9ybUJyb3dzZXJEeW5hbWljIiwiQXBwTW9kdWxlIiwiZW52aXJvbm1lbnQiLCJzIiwicHJvZHVjdGlvbiIsImIiLCJib290c3RyYXBNb2R1bGUiLCJjYXRjaCIsImVyciIsImYiLCJjb25zb2xlIiwiZXJyb3IiXSwic291cmNlcyI6WyJtYWluLnRzIl0sInNvdXJjZXNDb250ZW50IjpbImltcG9ydCB7IGVuYWJsZVByb2RNb2RlIH0gZnJvbSAnQGFuZ3VsYXIvY29yZSc7XHJcbmltcG9ydCB7IHBsYXRmb3JtQnJvd3NlckR5bmFtaWMgfSBmcm9tICdAYW5ndWxhci9wbGF0Zm9ybS1icm93c2VyLWR5bmFtaWMnO1xyXG5cclxuaW1wb3J0IHsgQXBwTW9kdWxlIH0gZnJvbSAnLi9hcHAvYXBwLm1vZHVsZSc7XHJcbmltcG9ydCB7IGVudmlyb25tZW50IH0gZnJvbSAnLi9lbnZpcm9ubWVudHMvZW52aXJvbm1lbnQnO1xyXG5cclxuaWYgKGVudmlyb25tZW50LnByb2R1Y3Rpb24pIHtcclxuICBlbmFibGVQcm9kTW9kZSgpO1xyXG59XHJcblxyXG5wbGF0Zm9ybUJyb3dzZXJEeW5hbWljKCkuYm9vdHN0cmFwTW9kdWxlKEFwcE1vZHVsZSlcclxuICAuY2F0Y2goZXJyID0+IGNvbnNvbGUuZXJyb3IoZXJyKSk7XHJcbiJdLCJtYXBwaW5ncyI6Ijs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7SUFlWTtJQUFBQSxjQUFBLFlBQUFBLENBQUE7TUFBQSxPQUFBQyxjQUFBO0lBQUE7RUFBQTtFQUFBLE9BQUFBLGNBQUE7QUFBQTtBQUFBRCxjQUFBO0FBZlosU0FBU0UsY0FBYyxRQUFRLGVBQWU7QUFDOUMsU0FBU0Msc0JBQXNCLFFBQVEsbUNBQW1DO0FBRTFFLFNBQVNDLFNBQVMsUUFBUSxrQkFBa0I7QUFDNUMsU0FBU0MsV0FBVyxRQUFRLDRCQUE0QjtBQUFDTCxjQUFBLEdBQUFNLENBQUE7QUFFekQsSUFBSUQsV0FBVyxDQUFDRSxVQUFVLEVBQUU7RUFBQVAsY0FBQSxHQUFBUSxDQUFBO0VBQUFSLGNBQUEsR0FBQU0sQ0FBQTtFQUMxQkosY0FBYyxDQUFDLENBQUM7QUFDbEIsQ0FBQztFQUFBRixjQUFBLEdBQUFRLENBQUE7QUFBQTtBQUFBUixjQUFBLEdBQUFNLENBQUE7QUFFREgsc0JBQXNCLENBQUMsQ0FBQyxDQUFDTSxlQUFlLENBQUNMLFNBQVMsQ0FBQyxDQUNoRE0sS0FBSyxDQUFDQyxHQUFHLElBQUk7RUFBQVgsY0FBQSxHQUFBWSxDQUFBO0VBQUFaLGNBQUEsR0FBQU0sQ0FBQTtFQUFBLE9BQUFPLE9BQU8sQ0FBQ0MsS0FBSyxDQUFDSCxHQUFHLENBQUM7QUFBRCxDQUFDLENBQUMiLCJpZ25vcmVMaXN0IjpbXX0=