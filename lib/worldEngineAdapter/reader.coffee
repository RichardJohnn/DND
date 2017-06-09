fs       = require 'fs'
join     = require('path').join
ProtoBuf = require 'protobufjs'

_worldProto = ->
  path = join(__dirname, "World.proto")
  builder = ProtoBuf.loadProtoFile(path)
  builder.build("World").World

worldReader = () ->
  path = join(__dirname, '../assets/worldgenerator/Cyriev.world')
  file = fs.readFileSync(path)
  _worldProto().decode(file)

module.exports = worldReader
