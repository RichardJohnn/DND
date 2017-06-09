expect = require('chai').expect
container = require '../../deps'

describe 'Name Generator', ->
  beforeEach ->
    @nameGenerator = container.NameGenerator

  it 'creates a name', (done) ->
    samples = ['all', 'apple', 'art']
    @nameGenerator samples, 10, (err, [result]) ->
      console.log result
      expect(result).to.have.length 10
      expect(result[0].name[0]).to.equal 'A'
      done()


