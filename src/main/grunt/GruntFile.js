module.exports = function(grunt) {
	var path = require("path");

	var getAbsoluteDir2 = function(dir) {
		return path.isAbsolute(dir) ? dir : path.resolve(process.cwd(), dir);
	};

	var buildDirConfig = {
		assetsDirectory : getAbsoluteDir2(grunt.option("assetsDirectory")),
		assetsDirectoryRelative : path.basename(grunt.option("assetsDirectory")),

		lessDirectory : getAbsoluteDir2(grunt.option("lessDirectory")),
	};

	grunt.initConfig({
		globalConfig : buildDirConfig,
		pkg : grunt.file.readJSON("package.json"),
		bootstrap : grunt.file.readJSON("node_modules/bootstrap/package.json"),
		banner : "/*!\n" + " * <%= pkg.name %> v${project.version}\n" + " * Homepage: <%= pkg.homepage %>\n"
				+ " * Copyright 2016-<%= grunt.template.today(\"yyyy\") %> <%= pkg.author %> and others\n" + " * Licensed under <%= pkg.license %>\n" + "*/\n",
		copy : {
			build : {
				files : [
						{
							cwd : "node_modules/",
							dest : "<%=globalConfig.assetsDirectory%>/fonts/",
							expand : true,
							flatten : true,
							src : [ "bootstrap/dist/fonts/*", "font-awesome/fonts/*" ],
						},
						{
							cwd : "node_modules/",
							dest : "<%=globalConfig.assetsDirectory%>/js/",
							expand : true,
							flatten : true,
							src : [ "angular/*.min.*", "angular-animate/*.min.*", "angular-modal-service/dst/*.min.*", "angular-route/*.min.*",
									"angular-sanitize/*.min.*", "angular-translate/dist/*.min.*", "angular-translate-loader-static-files/*.min.*",
									"bootstrap/dist/js/*min.js", "html5shiv/dist/*min.js", "jquery/dist/*min.js", "respond.js/dest/*min.js" ],
						} ]
			}
		},
		googlefonts : {
			build : {
				options : {
					fontPath : "<%=globalConfig.assetsDirectory%>/fonts/",
					cssFile : "<%=globalConfig.assetsDirectory%>/css/fonts.css",
					httpPath : "../fonts/",
					fonts : [ {
						family : "Titillium Web",
						styles : [ 200, 400, 600, 700, "200italic", "400italic", "600italic", "700italic" ]
					}, {
						family : "Source Code Pro",
						styles : [ 400, 300, 500 ]
					} ]
				}
			}
		},
		jshint : {
			all : [ "Gruntfile.js", "<%=globalConfig.assetsDirectory%>/js/main.js" ],
			options : {
				jshintrc : ".jshintrc"
			}
		},
		less : {
			build : {
				options : {
					compress : true,
					ieCompat : false,
					outputSourceFiles : true,
					modifyVars : {
						// font-awesome
						"fa-font-path" : "\"../fonts\"",
						// bootstrap
						"icon-font-path" : "\"../fonts/\"",
						"font-family-sans-serif" : "\"Titillium Web\", sans-serif",
						"brand-primary" : "orange",
						// "brand-success" : "#5cb85c",
						// "brand-warning" : "#f0ad4e",
						// "brand-danger" : "#d9534f",
						// "brand-info" : "#5bc0de",
						"panel-default-heading-bg" : "#fafafa",
						"input-border-focus" : "@brand-primary",
						"text-color" : "#5a5a5a"
					}
				},
				files : [ {
					expand : true,
					cwd : "<%=globalConfig.lessDirectory%>/",
					src : [ "*.less" ],
					dest : "<%=globalConfig.assetsDirectory%>/css/",
					ext : ".css"
				} ]
			}
		}
	});

	grunt.loadNpmTasks("grunt-contrib-concat");
	grunt.loadNpmTasks("grunt-contrib-copy");
	grunt.loadNpmTasks("grunt-contrib-jshint");
	grunt.loadNpmTasks("grunt-contrib-less");
	grunt.loadNpmTasks("grunt-eslint");
	grunt.loadNpmTasks("grunt-google-fonts");

	grunt.registerTask("test", [ "jshint" ]);
	grunt.registerTask("default", "build static webapp resources", [ "test", "copy", "googlefonts", "less" ]);
};