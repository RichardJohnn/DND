worldReader = require './lib/worldEngineAdapter/reader'
world = worldReader()
keys = Object.keys(world)
module.exports = { world: world, keys: keys}


