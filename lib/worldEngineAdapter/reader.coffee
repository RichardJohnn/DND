fs = require 'fs'
join = require('path').join
ProtoBuf = require 'protobufjs'

worldReader = (_path) ->
  builder = ProtoBuf.loadProtoFile(join(__dirname, "World.proto"))
  World = builder.build("World").World
  w = new World()

  file = fs.readFileSync(_path)

  World.decode(file)

module.exports = worldReader
