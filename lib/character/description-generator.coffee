DescriptionGenerator = (@personality) ->
  =>
    me = {}
    keys = Object.keys(@personality)
    for key in keys
      list = @personality[key]
      length = list.length
      index = Math.floor(Math.random() * length)
      trait = list[index]
      me[key] = trait

    me.description = "#{me.positive}, #{me.neutral}, but #{me.negative}"

    me

module.exports = DescriptionGenerator
