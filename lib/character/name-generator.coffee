python = require 'python-shell'

NameGenerator = ->
  (samples, numberToMake, cb) ->
    python.run 'name-generator.py', {
      mode: 'json'
      args: [numberToMake].concat(samples)
      scriptPath: __dirname
    }, cb

module.exports = NameGenerator
