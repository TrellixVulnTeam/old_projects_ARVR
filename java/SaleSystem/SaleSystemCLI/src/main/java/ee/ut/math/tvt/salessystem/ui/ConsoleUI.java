package ee.ut.math.tvt.salessystem.ui;

import ee.ut.math.tvt.salessystem.ProjectProperties;
import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.Receipt;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import ee.ut.math.tvt.salessystem.logic.Warehouse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

/**
 * A simple CLI (limited functionality).
 */
public class ConsoleUI {

    private static final Logger log = LogManager.getLogger(ConsoleUI.class);

    private final SalesSystemDAO dao;
    private final Warehouse warehouse;
    private final ShoppingCart cart;
    @SuppressWarnings("SpellCheckingInspection")
    private static final String LINE_SEPAR = "-------------------------";
    private static final String INPUT_ARGUMENT_ERROR_MESSAGE = "Incorrect argument input!";
    private boolean quit = false;

    public ConsoleUI(SalesSystemDAO dao) {
        this.dao = dao;
        warehouse = new Warehouse(dao);
        cart = new ShoppingCart(dao);
    }

    public static void main(String[] args) throws Exception {
        log.info(() -> String.format("javafx version: %s", System.getProperty("javafx.runtime.version")));

        SalesSystemDAO dao = new InMemorySalesSystemDAO();
        ConsoleUI console = new ConsoleUI(dao);
        console.run();
    }

    /**
     * Run the sales system CLI.
     */
    public void run() throws IOException {
        log.info("ConsoleUI started");
        log.debug(()->"===========================");
        log.debug(()->"=       Sales System      =");
        log.debug(()->"===========================");
        printUsage();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            while (!quit) {
                log.debug(()->"");
                processCommand(in.readLine().trim());
                log.debug(()->"Done. ");
            }
            dao.close();
        }
    }

    private void processCommand(String command) {
        log.debug(() -> String.format("Inserted CLI command: %s", command));

        String[] c = command.replaceAll("\\s{2,}", " ").split(" ");
        c[0] = c[0].toLowerCase();

        switch (c[0]) {
            case "h":
                printUsage();
                break;
            case "q":
                log.info("ConsoleUI closed");
                quit = true;
                break;
            case "w":
                processWarehouse(c);
                break;
            case "c":
                showCart();
                break;
            case "p":
                if (cart.isEmpty()) {
                    log.debug(()->"System won't confirm an empty cart");
                } else cart.submitCurrentPurchase();
                break;
            case "r":
                cart.cancelCurrentPurchase();
                break;
            case "t":
                showTeam();
                break;
            case "rm":
                removeFromCart(c[1]);
                break;
            case "a":
                addToCart(c);
                break;
            case "hist":
                processHistory(c);
                break;
            default:
                log.debug(()->"Unknown command");
                break;
        }
    }

    private void showStock(boolean compact) {
        List<StockItem> stockItems = dao.findStockItems();
        log.debug(()->LINE_SEPAR);
        for (StockItem si : stockItems) {
            if (!compact) {
                System.out.printf("%d %s %sEuro (%d pcs)%n", si.getId(), si.getName(), si.getPrice(), si.getQuantity());
            } else {
                log.debug(si::toString);
            }
        }
        if (stockItems.isEmpty()) {
            log.debug(()->"\tNothing");
        }
        log.debug(()->LINE_SEPAR);
    }

    private void showCart() {
        log.debug(()->LINE_SEPAR);
        for (SoldItem si : cart.getAll()) {
            System.out.printf("%d %s %sEuro (%d pcs)%n", si.getId(), si.getName(), si.getPrice(), si.getQuantity());
        }
        if (cart.isEmpty()) {
            log.debug(()->"\tNothing");
        } else {
            log.debug(()->"\tSum: " + cart.getSum() + "Euro");
        }
        log.debug(()->LINE_SEPAR);
    }

    private void printUsage() {
        log.debug(()->LINE_SEPAR);
        log.debug(()->"Usage:");
        log.debug(()->"h\t\tShow this help");

        log.debug(()->"w\t\tShow warehouse contents");
        log.debug(()->" \t\t\t-c Show compact representation");
        log.debug(()->" \t\t\t-m <id,product_name,product_description,price,quantity>");
        log.debug(()->" \t\t\t^This modifies an existing stock item or creates a new one if none are found with a matching id");

        log.debug(()->"hist\tShow all purchase history");
        log.debug(()->"hist NUM\n\tShow NUM last receipts");
        log.debug(()->"hist DATE\n\tShow purchase history from DATE day (yyyy-mm-dd format)");
        log.debug(()->"hist START END\n\tShow purchase history from START to END (yyyy-mm-dd format)");
        log.debug(()->"c\t\tShow cart contents");
        log.debug(()->"a IDX NR\n\tAdd NR of stock item with index IDX to the cart");
        log.debug(()->"rm IDX\t\tRemove item with index IDX from te cart");
        log.debug(()->"p\t\tPurchase the shopping cart");
        log.debug(()->"r\t\tReset the shopping cart");
        log.debug(()->"t\t\tShow information about the team");
        log.debug(()->"q\t\tQuit");
        log.debug(()->LINE_SEPAR);
    }


    private void processHistory(String[] c) {
        List<Receipt> receipts;
        try {
            if (c.length == 1) {
                receipts = dao.getReceipts();
                log.info(() -> "Retreived all receipts");
            } else if (c.length == 2) {
                try {
                    final int n = Integer.parseInt(c[1]);
                    receipts = dao.getReceipts(n);
                    log.info(() -> "Retreived last " + n + " receipts");
                } catch (NumberFormatException ignored) {
                    LocalDate date = LocalDate.parse(c[1]);
                    receipts = dao.getReceipts(date, date);
                    log.info(() -> "Retreived receipts at " + date.toString());
                }
            } else {
                receipts = dao.getReceipts(LocalDate.parse(c[1]), LocalDate.parse(c[2]));
                log.info(() -> "Retreived receipts between " + c[1] + " and " + c[2]);
            }
            if (!receipts.isEmpty()) {
                for (Receipt receipt : receipts) {
                    log.debug(receipt::toString);
                    for (SoldItem item : receipt.getItems()) {
                        System.out.printf("\t\t%s %sEuro (%d pcs)%n", item.getName(), item.getPrice(), item.getQuantity());
                    }
                }
            } else {
                log.debug(()->"No purchases made in the specified period.");
            }
        } catch (SalesSystemException e) {
            log.debug(e::getMessage);
        } catch (NoSuchElementException | DateTimeParseException e) {
            log.debug(()->INPUT_ARGUMENT_ERROR_MESSAGE);
        }
    }

    private void showTeam() {
        log.debug(()->LINE_SEPAR);
        Properties properties = ProjectProperties.get();
        if (properties == null) {
            log.debug(()->"Error while reading properties file");
            log.debug(()->LINE_SEPAR);
            return;
        }
        String message = "%20s%50s\n";
        System.out.format(message, (Object[]) new String[]{"Team name:", properties.getProperty("name")});
        System.out.format(message, (Object[]) new String[]{"Team leader:", properties.getProperty("leader")});
        System.out.format(message, (Object[]) new String[]{"Team leader email:", properties.getProperty("email")});
        System.out.format(message, (Object[]) new String[]{"Team members:", properties.getProperty("members")});
        log.debug(()->LINE_SEPAR);
    }

    private void removeFromCart(String s) {
        try {
            int idx = Integer.parseInt(s);
            if (!cart.removeItem(idx)) {
                log.debug(()->"No item with the ID " + idx + " in cart.");
            }
        } catch (SalesSystemException e) {
            log.debug(e::getMessage);
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            log.debug(()->INPUT_ARGUMENT_ERROR_MESSAGE);
        }
    }

    private void addToCart(String[] c) {
        if (c.length != 3) return;
        try {
            long idx = Long.parseLong(c[1]);
            StockItem item = dao.findStockItem(idx);
            if (item != null) {
                cart.addItem(
                        SoldItem.parseSoldItem(
                                item,
                                c[2]));
            } else {
                log.debug(()->"No stock item with id " + idx);
            }
        } catch (SalesSystemException e) {
            log.debug(e::getMessage);
        } catch (NoSuchElementException | NumberFormatException e) {
            log.debug(()->INPUT_ARGUMENT_ERROR_MESSAGE);
        }
    }

    private void processWarehouse(String[] c) {
        boolean compact = false;
        for (int i = 1; i < c.length; i++) {
            if (c[i].equals("-c")) compact = true;
            if (c[i].equals("-m")) {
                try {
                    String[] args = c[i + 1].split(",");
                    warehouse.processStockItem(
                            StockItem.parseStockItem(
                                    args[0],
                                    args[1].replace("_", " "),
                                    args[2].replace("_", " "),
                                    args[3],
                                    args[4]));
                    log.debug(()->"Successfully modified " + args[1]);
                } catch (SalesSystemException e) {
                    log.debug(e::getMessage);
                } catch (ArrayIndexOutOfBoundsException e) {
                    log.debug(()->"Expected 5 arguments but got " + e.getMessage().split("for length ")[1] + "!");
                }
            }
        }
        showStock(compact);
    }
}
