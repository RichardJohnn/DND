fs = require 'fs'
join = require('path').join
ProtoBuf = require 'protobufjs'

_worldProto = ->
  builder = ProtoBuf.loadProtoFile(join(__dirname, "World.proto"))
  builder.build("World").World

worldReader = (_path) ->
  file = fs.readFileSync(_path)
  _worldProto().decode(file)

module.exports = worldReader
