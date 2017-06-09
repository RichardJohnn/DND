REPORTER = spec

all: coffee-jshint test

clean:
	rm -rf ./node_modules

test:
	@NODE_ENV=test ./node_modules/.bin/mocha --recursive --reporter $(REPORTER) --timeout 11000

coffee-jshint:
	coffee-jshint lib examples test index.coffee

tests: test

tap:
	@NODE_ENV=test ./node_modules/.bin/mocha -R tap > results.tap

unit:
	@NODE_ENV=test ./node_modules/.bin/mocha --recursive -R xunit > results.xml --timeout 3000

skel:
	mkdir examples lib test
	touch index.coffee
	npm install mocha chai --save-dev

.PHONY: test tap unit coffee-jshint skel
