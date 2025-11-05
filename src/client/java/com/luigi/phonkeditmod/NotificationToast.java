package com.luigi.phonkeditmod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Notificação estilo "toast" do Minecraft (como conquistas)
 * Aparece no canto superior direito e desaparece automaticamente
 */
public class NotificationToast implements Toast {
	private static final Identifier TEXTURE = Identifier.ofVanilla("toast/advancement");
	private static final int DISPLAY_TIME = 6000; // 6 segundos
	private static final Identifier ICON_TEXTURE = Identifier.of("phonk-edit-mod", "icon.png");
	
	private final Text title;
	private final Text description;
	private long startTime;
	private boolean justUpdated;
	
	public NotificationToast(String title, String description) {
		this.title = Text.literal(title);
		this.description = Text.literal(description);
	}
	
	@Override
	public int getWidth() {
		return 160; // Largura aumentada (padrão é 160)
	}
	
	@Override
	public int getHeight() {
		return 32; // Altura padrão do toast
	}
	
	@Override
	public Visibility draw(DrawContext context, ToastManager manager, long currentTime) {
		if (this.justUpdated) {
			this.startTime = currentTime;
			this.justUpdated = false;
		}
		
		// Desenha o fundo (textura de conquista)
		context.drawGuiTexture(TEXTURE, 0, 0, this.getWidth(), this.getHeight());
		
		// Desenha o título (linha 1)
		context.drawText(manager.getClient().textRenderer, this.title, 30, 7, 0xFFFF00, false);
		
		// Desenha a descrição (linha 2)
		context.drawText(manager.getClient().textRenderer, this.description, 30, 18, 0xFFFFFF, false);
		
		// Desenha um ícone (opcional - usando caveira do mod)
		context.drawTexture(ICON_TEXTURE, 8, 8, 0, 0, 16, 16, 16, 16);
		
		// Verifica se deve desaparecer
		long elapsedTime = currentTime - this.startTime;
		return elapsedTime >= DISPLAY_TIME ? Visibility.HIDE : Visibility.SHOW;
	}
	
	/**
	 * Mostra uma notificação de áudios carregados
	 */
	public static void showAudioLoaded(int count) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client != null && client.getToastManager() != null) {
			String title = "§6Phonk Edit Mod";
			String description = count > 0 
				? "§a" + count + " §7audio" + (count > 1 ? "s" : "") + " loaded"
				: "§cNo audio found";
			client.getToastManager().add(new NotificationToast(title, description));
		}
	}
	
	/**
	 * Mostra uma notificação de imagens carregadas
	 */
	public static void showImagesLoaded(int count) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client != null && client.getToastManager() != null) {
			String title = "§6Phonk Edit Mod";
			String description = count > 0 
				? "§a" + count + " §7image" + (count > 1 ? "s" : "") + " loaded"
				: "§cNo images found";
			client.getToastManager().add(new NotificationToast(title, description));
		}
	}
	
	/**
	 * Mostra uma notificação de erros em imagens
	 */
	public static void showImageErrors(int errorCount) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client != null && client.getToastManager() != null) {
			String title = "§cPhonk Edit Mod";
			String description = "§c" + errorCount + " §7invalid image" + (errorCount > 1 ? "s" : "") + " (PNG only!)";
			client.getToastManager().add(new NotificationToast(title, description));
		}
	}
	
	/**
	 * Mostra uma notificação de arquivos de áudio inválidos
	 */
	public static void showAudioErrors(int errorCount) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client != null && client.getToastManager() != null) {
			String title = "§cPhonk Edit Mod";
			String description = "§c" + errorCount + " §7invalid audio" + (errorCount > 1 ? "s" : "") + " (OGG only!)";
			client.getToastManager().add(new NotificationToast(title, description));
		}
	}
	
	/**
	 * Mostra uma notificação genérica
	 */
	public static void show(String title, String description) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client != null && client.getToastManager() != null) {
			client.getToastManager().add(new NotificationToast(title, description));
		}
	}
}
