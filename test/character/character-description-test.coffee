expect = require('chai').expect
container = require '../../deps'
_ = require 'lodash'

describe 'Character Description', ->
  beforeEach ->
    descriptionGenerator = container.DescriptionGenerator
    @generator  = descriptionGenerator()
    @generator2 = descriptionGenerator()

  describe 'characters', ->
    it 'creates a really interesting sounding person', ->
      expect(@generator.description).to.exist
      console.log @generator.description
      expect(@generator.description).to.not.equal(@generator2.description)

