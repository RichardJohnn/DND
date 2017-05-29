Bottle = require 'bottlejs'
bottle = new Bottle()

bottle.value '_',                       require('lodash')
bottle.value 'PersonalityTraits',       require('./lib/assets/personalityTraits')

bottle.service 'DescriptionGenerator',  require('./lib/character/description-generator'),  'PersonalityTraits'
bottle.service 'NameGenerator',         require('./lib/character/name-generator')
bottle.service 'PathFinder',            require('./lib/level/path-finder')

bottle.instanceFactory 'PickupHandler',   require('./lib/eventHandlers/pickup', '_')
bottle.instanceFactory 'MovementHandler', require('./lib/eventHandlers/movement', '_')
bottle.instanceFactory 'DropHandler',     require('./lib/eventHandlers/drop', '_')

bottle.instanceFactory 'Character',     require('./lib/character/character')

module.exports = bottle.container
