{ promisify } = require('bluebird')

python = require 'python-shell'
#{ run } = python
run = promisify(python.run)

NameGenerator = (samples, numberToMake) ->
  await run 'name-generator.py', {
    mode: 'json'
    args: [numberToMake].concat(samples)
    scriptPath: __dirname
  }

module.exports = NameGenerator
