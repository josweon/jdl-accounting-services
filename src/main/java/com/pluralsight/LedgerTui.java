package com.pluralsight;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.swing.AWTTerminalFontConfiguration;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;

import java.awt.Font;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class LedgerTui {
    //region Initializers
    private static MultiWindowTextGUI gui;
    private static BasicWindow mainWindow;   // the ONE window; screens swap inside it

    private static final TextColor GRAY      = new TextColor.RGB(212, 208, 200);
    private static final TextColor DARK_GRAY = new TextColor.RGB(128, 128, 128);
    private static final TextColor NAVY      = new TextColor.RGB(10, 36, 106);
    private static final TextColor WHITE     = new TextColor.RGB(255, 255, 255);
    private static final TextColor BLACK     = new TextColor.RGB(0, 0, 0);

    private static final String APP_NAME = "JDL Accounting Services";

    private static final String LOGO = """
                 ██ ██████  ██
                 ██ ██   ██ ██
                 ██ ██   ██ ██
            ██   ██ ██   ██ ██
             █████  ██████  ███████""";
    //endregion

    public static void main(String[] args) throws IOException {
        AccountingLedgerApp.loadTransactions();          // before the screen starts (it prints)
        Collections.sort(AccountingLedgerApp.ledger);

        Font font = new Font("Consolas", Font.PLAIN, 18);
        DefaultTerminalFactory factory = new DefaultTerminalFactory()
                .setTerminalEmulatorFontConfiguration(
                        new SwingTerminalFontConfiguration(
                                true,
                                AWTTerminalFontConfiguration.BoldMode.EVERYTHING,
                                font))
                .setInitialTerminalSize(new TerminalSize(100, 30));

        Screen screen = factory.createScreen();
        screen.startScreen();
        try {
            gui = new MultiWindowTextGUI(screen);
            gui.setTheme(SimpleTheme.makeTheme(
                    true,
                    BLACK, GRAY,
                    BLACK, WHITE,
                    WHITE, NAVY,
                    DARK_GRAY));

            mainWindow = new BasicWindow(APP_NAME);
            mainWindow.setHints(List.of(Window.Hint.FULL_SCREEN));

            showWelcomeScreen();
            gui.addWindowAndWait(mainWindow);            // runs until mainWindow.close()
        } finally {
            screen.stopScreen();
        }
    }

    //region Screen helpers
    private static void showScreen(String title, Panel content) {
        mainWindow.setTitle(APP_NAME + " - " + title);

        Panel center = new Panel(new GridLayout(1));
        center.addComponent(content, GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER, true, true));

        Panel footer = new Panel(new LinearLayout(Direction.HORIZONTAL));
        footer.setFillColorOverride(NAVY);
        footer.addComponent(new Label(" Tab/Arrows = Move   Enter = Select ")
                .setForegroundColor(WHITE)
                .setBackgroundColor(NAVY));

        Panel root = new Panel(new BorderLayout());
        root.addComponent(center, BorderLayout.Location.CENTER);
        root.addComponent(footer, BorderLayout.Location.BOTTOM);

        mainWindow.setComponent(root);
    }

    private static Button button(String label, Runnable action) {
        Button b = new Button(label, action);
        b.setRenderer(new Button.BorderedButtonRenderer());
        return b;
    }

    private static LayoutData centered() {
        return LinearLayout.createLayoutData(LinearLayout.Alignment.Center);
    }
    //endregion

    //region Screens
    private static void showWelcomeScreen() {
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));

        Label logo    = new Label(LOGO).setForegroundColor(NAVY);
        Label title   = new Label("A C C O U N T I N G   S E R V I C E S").addStyle(SGR.BOLD);
        Label tagline = new Label("Track every deposit. Every payment.").setForegroundColor(DARK_GRAY);

        Button start = button("Start", LedgerTui::showHomeScreen);
        Button exit  = button("Exit", mainWindow::close);

        Panel buttons = new Panel(new LinearLayout(Direction.HORIZONTAL));
        buttons.addComponent(start);
        buttons.addComponent(exit);

        panel.addComponent(logo, centered());
        panel.addComponent(new EmptySpace());
        panel.addComponent(title, centered());
        panel.addComponent(new Separator(Direction.HORIZONTAL),
                LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        panel.addComponent(tagline, centered());
        panel.addComponent(new EmptySpace());
        panel.addComponent(buttons, centered());

        showScreen("Welcome", panel);
        start.takeFocus();
    }

    /** Placeholder until step 2. */
    private static void showHomeScreen() {
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));
        panel.addComponent(new Label("H O M E").addStyle(SGR.BOLD), centered());
        panel.addComponent(new Label("Menu coming in step 2.").setForegroundColor(DARK_GRAY), centered());
        panel.addComponent(new EmptySpace());

        Button back = button("Back", LedgerTui::showWelcomeScreen);
        Panel buttons = new Panel(new LinearLayout(Direction.HORIZONTAL));
        buttons.addComponent(back);
        buttons.addComponent(button("Exit", mainWindow::close));
        panel.addComponent(buttons, centered());

        showScreen("Home", panel);
        back.takeFocus();
    }
    //endregion
}
