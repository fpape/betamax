grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.dependency.resolution = {
	inherits "global"
	log "warn"
	repositories {
		grailsPlugins()
		grailsHome()
		grailsCentral()
		mavenLocal()
		mavenCentral()
		mavenRepo "http://oss.sonatype.org/content/groups/public/"
		mavenRepo "http://m2repo.spockframework.org/snapshots/"
	}
	dependencies {
		compile("org.codehaus.groovy.modules.http-builder:http-builder:0.5.1") {
			excludes "groovy", "xml-apis"
		}
		test "com.github.robfletcher:betamax:1.1-SNAPSHOT"
	}
	plugins {
		compile ":hibernate:$grailsVersion"
		compile ":jquery:1.6.1.1"
		compile ":resources:1.0.2"
		build ":tomcat:$grailsVersion"
//		test ":spock:0.6-SNAPSHOT" // use this to run with Grails 2
		test ":spock:0.5-groovy-1.7"
	}
}
