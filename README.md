### Formatter syntax
#### All syntax can be combined with other events.

<p align="center">
  <img width="auto" height="auto" src="https://imgur.com/MJojshJ.png">
  <img width="auto" height="auto" src="https://imgur.com/Rayaw0O.png">
</p>
<p align="center">
  <img width="auto" height="auto" src="https://imgur.com/fvN5u59.png">
</p>

Description        | Syntax                                 | More Info
 -------------------|----------------------------------------|----
General syntax     |` ${Text}(parameter=parm)         `| [ClickEvent.Action](https://ci.md-5.net/job/BungeeCord/ws/chat/target/apidocs/net/md_5/bungee/api/chat/ClickEvent.Action.html), [HoverEvent.Action](https://ci.md-5.net/job/BungeeCord/ws/chat/target/apidocs/net/md_5/bungee/api/chat/HoverEvent.Action.html)
Link               |` ${Text}(open_url=google.com)    `| Open URL in browser
Suggest command    |` ${Text}(suggest_command=/ping)  `| Suggest command on click
Run command        |` ${Text}(run_command=/ping)      `| Run command as player
RGB Hex Color      |` ${Text}(color=#ffffff)          `| Apply hex color to text
RGB Color Gradient |` ${Text}(color=#ffffff-#ffffff)  `| Gradient of two colors. (Supports all color forms)
Text format        |` ${Text}(format=underline)       `| Apply text format to the text
Hover text |` ${Text}(show_text=Click to join)        `| Show text when hovering over the text

<p align="center">
  <img width="auto" height="auto" src="https://imgur.com/zdiQFKV.png">
</p>