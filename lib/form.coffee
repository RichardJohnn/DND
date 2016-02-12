blessed = require 'blessed'

form = blessed.form
  parent: screen
  keys: true
  left: 0
  top: 0
  width: 30
  height: 4
  bg: 'green'
  content: 'Submit or cancel?'

submit = blessed.button(
  parent: form
  mouse: true
  keys: true
  shrink: true
  padding: ->
    left: 1
    right: 1
  left: 10
  top: 2
  shrink: true
  name: 'submit'
  content: 'submit'
  style: ->
    bg: 'blue'
    focus: ->
      bg: 'red'
    hover: ->
      bg: 'red'
)

cancel = blessed.button(
  parent: form
  mouse: true
  keys: true
  shrink: true
  padding: ->
    left: 1
    right: 1
  left: 20
  top: 2
  shrink: true
  name: 'cancel'
  content: 'cancel'
  style: ->
    bg: 'blue'
    focus: ->
      bg: 'red'
    hover: ->
      bg: 'red'
)

submit.on('press', () ->
  form.submit()
)

cancel.on('press', () ->
  form.reset()
)

form.on('submit', (data) ->
  form.setContent('Submitted.')
  screen.render()
)

form.on('reset', (data) ->
  form.setContent('Canceled.')
  screen.render()
)
