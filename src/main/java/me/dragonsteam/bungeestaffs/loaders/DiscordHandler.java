package me.dragonsteam.bungeestaffs.loaders;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * Class for project BungeeStaffs
 * Date: 08/01/2022 - 16:19.
 *
 * @author Joansiitoh
 */
@Getter @Setter
@Accessors(chain = true)
public class DiscordHandler {

    ///////////////////////////////////////////////////////////////////////////

    private boolean embedEnabled;

    private String authorName, authorUrl, authorIcon;
    private String footerText, footerIcon;
    private String titleText, titleUrl;
    private String image, thumbnail;
    private String format, channel;
    private int color;

    ///////////////////////////////////////////////////////////////////////////

    public DiscordHandler(@Nullable Configuration config) {
        if (config != null) {
            this.format = config.getString("FORMAT", null);
            this.channel = config.getString("CHANNEL", null);

            this.embedEnabled = config.getBoolean("EMBED.ENABLED", false);

            this.authorName = config.getString("EMBED.AUTHOR.NAME", null);
            this.authorUrl = config.getString("EMBED.AUTHOR.URL", null);
            this.authorIcon = config.getString("EMBED.AUTHOR.ICON", null);

            this.footerText = config.getString("EMBED.FOOTER.TEXT", null);
            this.footerIcon = config.getString("EMBED.FOOTER.ICON", null);

            this.titleText = config.getString("EMBED.TITLE.TEXT", null);
            this.titleUrl = config.getString("EMBED.TITLE.URL", null);

            this.image = config.getString("EMBED.IMAGE", null);
            this.thumbnail = config.getString("EMBED.THUMBNAIL", null);

            this.color = config.getInt("EMBED.COLOR", 0);
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    public MessageEmbed build(String format) {
        EmbedBuilder builder = new EmbedBuilder();

        if (authorName != null) builder.setAuthor(authorName, authorUrl, authorIcon);
        if (footerText != null) builder.setFooter(footerText, footerIcon);
        if (titleText != null) builder.setTitle(titleText, titleUrl);
        if (image != null) builder.setImage(image);
        if (thumbnail != null) builder.setThumbnail(thumbnail);
        if (color != 0) builder.setColor(color);
        if (format != null) builder.setDescription(replace(format));

        return builder.build();
    }

    private String replace(String text) {
        return text;
    }
}
