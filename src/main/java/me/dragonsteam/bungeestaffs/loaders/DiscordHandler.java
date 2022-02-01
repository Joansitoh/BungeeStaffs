package me.dragonsteam.bungeestaffs.loaders;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.md_5.bungee.config.Configuration;

import javax.annotation.Nullable;

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

        // Removing blank boxes.
        if (this.authorName != null && this.authorName.equals("")) this.authorName = null;
        if (this.authorUrl != null && this.authorUrl.equals("")) this.authorUrl = null;
        if (this.authorIcon != null && this.authorIcon.equals("")) this.authorIcon = null;

        if (this.footerText != null && this.footerText.equals("")) this.footerText = null;
        if (this.footerIcon != null && this.footerIcon.equals("")) this.footerIcon = null;

        if (this.titleText != null && this.titleText.equals("")) this.titleText = null;
        if (this.titleUrl != null && this.titleUrl.equals("")) this.titleUrl = null;

        if (this.image != null && this.image.equals("")) this.image = null;
        if (this.thumbnail != null && this.thumbnail.equals("")) this.thumbnail = null;
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
