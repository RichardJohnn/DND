Bottle = require 'bottlejs'
bottle = new Bottle()

bottle.value 'PersonalityTraits', require('./lib/assets/personalityTraits')
bottle.service 'DescriptionGenerator', require('./lib/character/description-generator'), 'PersonalityTraits'
bottle.service 'NameGenerator', require('./lib/character/name-generator')

module.exports = bottle.container
