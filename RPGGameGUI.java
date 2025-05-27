import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

enum Job {
    騎士,
    魔法使い
}

class Character {
    String name;
    Job job;
    int level;
    int health;
    int maxHealth;
    int attackPower;
    int magicPower; // ★追加
    int experience;
    Weapon weapon;
    Armor armor;
    List<String> skills = new ArrayList<>();
    List<String> inventory = new ArrayList<>();
    List<Weapon> weapons = new ArrayList<>();
    List<Armor> armors = new ArrayList<>();

    Character(String name, Job job) {
        this.name = name;
        this.job = job;
        this.level = 1;
        this.maxHealth = 100;
        this.health = maxHealth;
        this.attackPower = 10;
        this.magicPower = (job == Job.魔法使い) ? 15 : 0; // ★魔法使いなら初期値15
        this.experience = 0;
        // ★初期装備は「素手」と「布の服」のみ
        Weapon fist = new Weapon("素手", 0);
        weapons.add(fist);
        weapon = fist;
        Armor cloth = new Armor("布の服", 0, 0);
        armors.add(cloth);
        armor = cloth;
        // 魔法使いなら木の杖を初期装備に追加
        if (job == Job.魔法使い) {
            Weapon staff = new Weapon("木の杖", 5);
            weapons.add(staff);
            weapon = staff;
        }
        learnInitialSkills();
    }

    void learnInitialSkills() {
        if (job == Job.騎士) {
            skills.add("シールドブロック");
        } else if (job == Job.魔法使い) {
            skills.add("ファイアボール");
        }
    }

    void levelUp() {
        level++;
        attackPower += 5;
        maxHealth += 20;
        if (job == Job.魔法使い)
            magicPower += 7; // ★魔法使いは魔力も上昇
        health = maxHealth;
        experience = 0;
        // 騎士のスキルツリー例
        if (job == Job.騎士) {
            if (level == 2)
                skills.add("チャージ");
            if (level == 3)
                skills.add("パワースラッシュ");
        } else if (job == Job.魔法使い) {
            if (level == 2)
                skills.add("アイススピア");
            if (level == 3)
                skills.add("ライトニングボルト");
        }
    }

    // スキルの使用
    String useSkill(String skill, Enemy target) {
        if (!skills.contains(skill))
            return "そのスキルは使えません。";
        int weaponMagic = 0;
        // 杖なら武器のpowerを魔力に加算
        if (weapon != null && (weapon.name.contains("杖") || weapon.name.contains("ロッド"))) {
            weaponMagic = weapon.power;
        }
        switch (skill) {
            case "シールドブロック":
                health += 10;
                return name + "はシールドブロックで身を守った！（一時的にHP+10）";
            case "チャージ":
                if (target != null) {
                    int dmg = getAttackPower() + 10;
                    target.health -= dmg;
                    return name + "はチャージで" + target.name + "に" + dmg + "ダメージ！";
                }
                break;
            case "パワースラッシュ":
                if (target != null) {
                    int dmg = getAttackPower() + 20;
                    target.health -= dmg;
                    return name + "はパワースラッシュで" + target.name + "に" + dmg + "ダメージ！";
                }
                break;
            case "ファイアボール":
                if (target != null) {
                    int dmg = attackPower + magicPower + weaponMagic + 15;
                    target.health -= dmg;
                    return name + "はファイアボールで" + target.name + "に" + dmg + "ダメージ！";
                }
                break;
            case "アイススピア":
                if (target != null) {
                    int dmg = attackPower + magicPower + weaponMagic + 20;
                    target.health -= dmg;
                    return name + "はアイススピアで" + target.name + "に" + dmg + "ダメージ！";
                }
                break;
            case "ライトニングボルト":
                if (target != null) {
                    int dmg = attackPower + magicPower + weaponMagic + 25;
                    target.health -= dmg;
                    return name + "はライトニングボルトで" + target.name + "に" + dmg + "ダメージ！";
                }
                break;
        }
        return "スキルの使用に失敗しました。";
    }

    // 装備メソッド
    void equipWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    void equipArmor(Armor armor) {
        this.armor = armor;
    }

    // 攻撃力・防御力・最大HPの計算
    int getAttackPower() {
        return attackPower + (weapon != null ? weapon.power : 0);
    }

    int getDefense() {
        return armor != null ? armor.defense : 0;
    }

    int getMaxHealth() {
        return maxHealth + (armor != null ? armor.hpBonus : 0);
    }

    void heal() {
        int healAmount = new Random().nextInt(15) + 10;
        health += healAmount;
        if (health > maxHealth) {
            health = maxHealth;
        }
    }

    boolean isAlive() {
        return health > 0;
    }

    void learnSkill(String skill) {
        if (!skills.contains(skill)) {
            skills.add(skill);
        }
    }

    void addItem(String item) {
        inventory.add(item);
    }

    void useItem(String item) {
        if (inventory.contains(item)) {
            inventory.remove(item);
            if (item.equals("Health Potion")) {
                heal();
            }
        }
    }

    void gainExperience(int exp) {
        experience += exp;
        if (experience >= 20) { // 例: 20expでレベルアップ
            levelUp();
            experience = 0;
        }
    }

    // 攻撃メソッド修正
    void attack(Enemy enemy) {
        int damage = Math.max(1, getAttackPower() - enemy.getDefense());
        enemy.health -= damage;
    }
}

class Enemy {
    String name;
    int level;
    int health;
    int maxHealth;
    int attackPower;
    int defense; // ★防御力フィールドを追加

    Enemy(String name, int level) {
        this.name = name;
        this.level = level;
        this.maxHealth = 50 + level * 10;
        this.health = maxHealth;
        this.attackPower = 5 + level * 2;
        this.defense = 2 + level; // ★例：レベルに応じた防御力
    }

    int getDefense() {
        return defense;
    }

    void attack(Character target) {
        target.health -= attackPower;
    }

    boolean isAlive() {
        return health > 0;
    }
}

class InventoryDialog extends JDialog {
    // private Character player;

    InventoryDialog(JFrame parent, Character player) {
        super(parent, "インベントリ", true);
        // this.player = player;
        setSize(300, 200);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JList<String> itemList = new JList<>(player.inventory.toArray(new String[0]));
        add(new JScrollPane(itemList), BorderLayout.CENTER);

        JButton useButton = new JButton("使用");
        useButton.addActionListener(_ -> {
            String selectedItem = itemList.getSelectedValue();
            if (selectedItem != null) {
                player.useItem(selectedItem);
                dispose();
            }
        });
        add(useButton, BorderLayout.SOUTH);
    }
}

class Weapon {
    String name;
    int power;

    Weapon(String name, int power) {
        this.name = name;
        this.power = power;
    }
}

class Armor {
    String name;
    int defense;
    int hpBonus;

    Armor(String name, int defense, int hpBonus) {
        this.name = name;
        this.defense = defense;
        this.hpBonus = hpBonus;
    }
}

// Roomクラスを拡張
class Room {
    boolean hasEnemy;
    boolean hasTreasure;
    boolean hasPitfall; // 追加
    boolean isVisited;
    String description;

    Room(boolean hasEnemy, boolean hasTreasure, boolean hasPitfall, String description) {
        this.hasEnemy = hasEnemy;
        this.hasTreasure = hasTreasure;
        this.hasPitfall = hasPitfall;
        this.isVisited = false;
        this.description = description;
    }

    // 既存のコンストラクタも残しておくと安全
    Room(boolean hasEnemy, boolean hasTreasure, String description) {
        this(hasEnemy, hasTreasure, false, description);
    }
}

public class RPGGameGUI extends JFrame {
    // クラスのフィールドとして宣言
    private JTextField nameField;
    private JButton startButton;
    private JTextPane gameArea; // ←型をJTextPaneに変更
    private Character player;
    private List<Enemy> enemies = new ArrayList<>();
    private JPanel actionPanel;
    private JButton attackButton;
    private JButton healButton;
    private JButton upButton;
    private JButton downButton;
    private JButton leftButton;
    private JButton rightButton;

    // RPGGameGUIクラス内
    private Room[][] dungeonMap;
    private int playerX = 0, playerY = 0;
    private JPanel mapPanel; // マップ表示用パネル
    private JLabel[][] mapLabels; // マップセル表示用

    // RPGGameGUIクラスのフィールドに追加
    private JProgressBar playerHpBar;
    private JProgressBar enemyHpBar;
    private JButton dialogInventoryButton;
    private JButton skillButton;
    private JLabel weaponLabel;
    private JLabel armorLabel;
    private JButton equipWeaponButton;
    private JButton equipArmorButton;
    private JLabel enemyImageLabel; // ★画像表示用ラベルを追加

    // コンストラクタ内で初期化
    public RPGGameGUI() {
        setTitle("RPGゲーム");
        setSize(1500, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. panelの初期化・コンポーネント追加
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1)); // 行数を1つ増やす

        panel.add(new JLabel("キャラクターの名前を入力してください:"));
        nameField = new JTextField();
        panel.add(nameField);
        // 職業選択
        JComboBox<String> jobBox = new JComboBox<>(new String[] { "騎士", "魔法使い" });
        panel.add(new JLabel("職業を選択してください:"));
        panel.add(jobBox);
        startButton = new JButton("ゲーム開始");
        panel.add(startButton);

        // ★装備表示用パネルを作成し、横並びでまとめる
        weaponLabel = new JLabel("武器: ");
        equipWeaponButton = new JButton("武器変更");
        armorLabel = new JLabel("防具: ");
        equipArmorButton = new JButton("防具変更");

        JPanel equipPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        equipPanel.add(weaponLabel);
        equipPanel.add(equipWeaponButton);
        equipPanel.add(armorLabel);
        equipPanel.add(equipArmorButton);

        // panelにまとめてadd（この1行だけ！）
        panel.add(equipPanel);

        // 2. actionPanelの初期化・ボタン追加
        actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));

        // HPバー用のパネル
        playerHpBar = new JProgressBar();
        playerHpBar.setStringPainted(true);
        playerHpBar.setForeground(Color.GREEN);

        enemyHpBar = new JProgressBar();
        enemyHpBar.setStringPainted(true);
        enemyHpBar.setForeground(Color.RED);

        // 3. hpPanelの初期化
        JPanel hpPanel = new JPanel();
        hpPanel.setLayout(new GridLayout(2, 1));
        hpPanel.add(playerHpBar);
        hpPanel.add(enemyHpBar);

        // 4. wrapperPanelの初期化・add
        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.Y_AXIS));
        wrapperPanel.add(hpPanel); // ①HPバー
        wrapperPanel.add(panel); // ②名前・職業・開始ボタン
        wrapperPanel.add(actionPanel); // ③行動選択ボタン

        add(wrapperPanel, BorderLayout.NORTH);

        // gameArea = new JTextArea();
        // gameArea.setEditable(false);
        // add(new JScrollPane(gameArea), BorderLayout.CENTER);

        gameArea = new JTextPane();
        gameArea.setEditable(false);
        gameArea.setFocusable(false);
        gameArea.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(gameArea);

        // ★画像ラベルの初期化を先に行う
        enemyImageLabel = new JLabel();
        enemyImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        enemyImageLabel.setVisible(false); // 最初は非表示

        // ★画像ラベルをgameAreaの上に配置
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(enemyImageLabel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String playerName = nameField.getText();
                if (playerName.isEmpty()) {
                    JOptionPane.showMessageDialog(RPGGameGUI.this, "名前を入力してください。", "エラー", JOptionPane.ERROR_MESSAGE);
                } else {
                    // 選択された職業に応じてJobを設定
                    Job selectedJob = jobBox.getSelectedIndex() == 0 ? Job.騎士 : Job.魔法使い;
                    startGame(playerName, selectedJob);
                }
            }
        });

        // 行動選択パネルの設定
        actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));

        attackButton = new JButton("攻撃");
        attackButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!enemies.isEmpty()) {
                    Enemy enemy = enemies.get(0); // 最初の敵を選ぶ
                    player.attack(enemy);
                    appendColoredText(player.name + "は" + enemy.name + "に攻撃した！\n", Color.BLACK);
                    appendColoredText(enemy.name + "の現在の体力: " + enemy.health + "\n", Color.BLACK);

                    if (!enemy.isAlive()) {
                        appendColoredText(enemy.name + "を倒した！\n", Color.BLACK);
                        player.gainExperience(10);
                        enemies.remove(enemy);
                    }

                    if (enemy.isAlive()) {
                        enemy.attack(player);
                        appendColoredText(enemy.name + "は" + player.name + "に攻撃した！\n", Color.BLACK);
                        appendColoredText(player.name + "の現在の体力: " + player.health + "\n", Color.BLACK);
                    }
                }
                checkGameStatus(); // ここだけでOK
            }
        });

        healButton = new JButton("回復");
        healButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                player.heal();
                appendColoredText(player.name + "は回復した！\n", Color.BLACK);
                appendColoredText(player.name + "の現在の体力: " + player.health + "\n", Color.BLACK);
                checkGameStatus(); // ここだけでOK
            }
        });

        // 行動選択パネル内でインベントリを開くボタン
        dialogInventoryButton = new JButton("アイテム使用");
        dialogInventoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // アイテム選択ダイアログを表示
                if (player.inventory.isEmpty()) {
                    JOptionPane.showMessageDialog(actionPanel, "インベントリにアイテムがありません。");
                    return;
                }
                String[] items = player.inventory.toArray(new String[0]);
                String selected = (String) JOptionPane.showInputDialog(
                        RPGGameGUI.this, // ←ここを修正
                        "使用するアイテムを選択してください:",
                        "アイテム使用",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        items,
                        items[0]);
                if (selected != null) {
                    player.useItem(selected);
                    appendColoredText(player.name + "は" + selected + "を使用した！\n", Color.BLACK);
                    appendColoredText(player.name + "の現在の体力: " + player.health + "\n", Color.BLACK);
                    checkGameStatus(); // ここだけでOK
                }
            }
        });

        // 行動選択パネルにスキルボタンを追加
        skillButton = new JButton("スキル");
        skillButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (player.skills.isEmpty()) {
                    JOptionPane.showMessageDialog(actionPanel, "スキルを習得していません。");
                    return;
                }
                String[] skillArray = player.skills.toArray(new String[0]);
                String selectedSkill = (String) JOptionPane.showInputDialog(
                        actionPanel,
                        "使用するスキルを選択してください:",
                        "スキル使用",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        skillArray,
                        skillArray[0]);
                if (selectedSkill != null) {
                    Enemy target = enemies.isEmpty() ? null : enemies.get(0);
                    String result = player.useSkill(selectedSkill, target);
                    appendColoredText(result + "\n", Color.BLACK);
                    if (target != null && !target.isAlive()) {
                        appendColoredText(target.name + "を倒した！\n", Color.BLACK);
                        player.gainExperience(10);
                        enemies.remove(target);
                    }
                    checkGameStatus(); // ここだけでOK
                }
            }
        });

        // 行動ボタン用パネル
        JPanel actionButtonPanel = new JPanel(new GridLayout(1, 3));
        actionButtonPanel.add(attackButton);
        actionButtonPanel.add(healButton);
        actionButtonPanel.add(skillButton);

        // 装備・アイテムボタン用パネル
        JPanel itemButtonPanel = new JPanel(new GridLayout(1, 1));
        itemButtonPanel.add(dialogInventoryButton);

        // まとめて1つのパネルに
        actionPanel.add(actionButtonPanel);
        actionPanel.add(itemButtonPanel);

        // 最初は非アクティブ
        setActionButtonsEnabled(false);

        // 画面下部や好きな場所に追加
        // wrapperPanelにaddしている場合は、wrapperPanel.add(actionPanel); のみでOK
        // 直接addする場合は下記のように
        // add(actionPanel, BorderLayout.NORTH);

        // ...コンストラクタ内で初期化...
        upButton = new JButton("↑");
        downButton = new JButton("↓");
        leftButton = new JButton("←");
        rightButton = new JButton("→");

        JPanel movePanel = new JPanel();
        movePanel.setLayout(new GridLayout(2, 3));
        movePanel.add(new JLabel("")); // 1行1列目（空白）
        movePanel.add(upButton); // 1行2列目（↑）
        movePanel.add(new JLabel("")); // 1行3列目（空白）
        movePanel.add(leftButton); // 2行1列目（←）
        movePanel.add(downButton); // 2行2列目（↓）★ここに追加
        movePanel.add(rightButton); // 2行3列目（→）

        add(movePanel, BorderLayout.SOUTH);

        // ボタンのアクション
        upButton.addActionListener(_ -> movePlayer(0, -1));
        downButton.addActionListener(_ -> movePlayer(0, 1));
        leftButton.addActionListener(_ -> movePlayer(-1, 0));
        rightButton.addActionListener(_ -> movePlayer(1, 0));

        // マップパネルの初期化
        mapPanel = new JPanel();
        mapPanel.setLayout(new GridLayout(5, 5)); // 5x5マップの場合
        mapLabels = new JLabel[5][5];
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                JLabel label = new JLabel(" ", SwingConstants.CENTER);
                label.setOpaque(true);
                label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                mapLabels[y][x] = label;
                mapPanel.add(label);
            }
        }

        // ★ここを追加
        add(mapPanel, BorderLayout.EAST);

        // HPバー用のパネルを作成
        playerHpBar = new JProgressBar();
        playerHpBar.setStringPainted(true);
        playerHpBar.setForeground(Color.GREEN);

        enemyHpBar = new JProgressBar();
        enemyHpBar.setStringPainted(true);
        enemyHpBar.setForeground(Color.RED);

        hpPanel = new JPanel();
        hpPanel.setLayout(new GridLayout(2, 1));
        hpPanel.add(playerHpBar);
        hpPanel.add(enemyHpBar);

        // wrapperPanelに全てまとめる
        wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.Y_AXIS));
        wrapperPanel.add(hpPanel); // ①HPバー
        wrapperPanel.add(panel); // ②名前・職業・開始ボタン
        wrapperPanel.add(actionPanel); // ③行動選択ボタン

        add(wrapperPanel, BorderLayout.NORTH); // 画面上部にまとめて配置

        // BorderLayout.NORTHへのaddは↑この1回だけにする
        // add(panel, BorderLayout.NORTH); や add(hpPanel, BorderLayout.NORTH); は削除

        // 武器変更ボタン
        equipWeaponButton.addActionListener(_ -> {
            String[] weaponNames = player.weapons.stream().map(w -> w.name).toArray(String[]::new);
            String selected = (String) JOptionPane.showInputDialog(
                    RPGGameGUI.this, // ←ここを修正
                    "装備する武器を選択してください:", "武器変更",
                    JOptionPane.PLAIN_MESSAGE, null, weaponNames, weaponNames[0]);
            if (selected != null) {
                for (Weapon w : player.weapons) {
                    if (w.name.equals(selected)) {
                        player.equipWeapon(w);
                        weaponLabel.setText("武器: " + w.name); // ★ここでラベル更新
                        break;
                    }
                }
                updateHpBars();
            }
        });

        // 防具変更ボタン
        equipArmorButton.addActionListener(_ -> {
            String[] armorNames = player.armors.stream().map(a -> a.name).toArray(String[]::new);
            String selected = (String) JOptionPane.showInputDialog(
                    RPGGameGUI.this, "装備する防具を選択してください:", "防具変更",
                    JOptionPane.PLAIN_MESSAGE, null, armorNames, armorNames[0]);
            if (selected != null) {
                for (Armor a : player.armors) {
                    if (a.name.equals(selected)) {
                        player.equipArmor(a);
                        armorLabel.setText("防具: " + a.name); // ★ここでラベル更新
                        break;
                    }
                }
                updateHpBars();
            }
        });
    }

    private void startGame(String playerName, Job job) {
        player = new Character(playerName, job);
        gameArea.setText(player.name + "が冒険を始めました！\n");

        // マップ生成
        generateMap(5, 5); // 例: 5x5マップ

        // ★スタート地点を訪問済みにして部屋イベントを即時発生させる
        Room startRoom = dungeonMap[playerY][playerX];
        startRoom.isVisited = true;
        appendColoredText("現在地: " + startRoom.description + "\n", Color.BLACK);
        // スタート地点のイベント処理
        if (startRoom.hasEnemy) {
            appendColoredText("敵が現れた！\n", Color.BLACK);
            Enemy enemy = new Enemy("モンスター", player.level);
            enemies = new ArrayList<>();
            enemies.add(enemy);
            startBattle();
        } else if (startRoom.hasTreasure) {
            appendColoredText("【宝箱を見つけた！】\n", Color.YELLOW, 22);
            Random rand = new Random();
            int treasureType = rand.nextInt(4);
            String result = "";
            switch (treasureType) {
                case 0:
                    player.addItem("Health Potion");
                    result = "【回復薬を手に入れた！】";
                    break;
                case 1:
                    Weapon newWeapon = new Weapon("銀の剣", 20);
                    player.weapons.add(newWeapon);
                    result = "新しい武器【銀の剣】を手に入れた！";
                    break;
                case 2:
                    Armor newArmor = new Armor("銀の鎧", 10, 30);
                    player.armors.add(newArmor);
                    result = "新しい防具【銀の鎧】を手に入れた！";
                    break;
                case 3:
                    Weapon apprenticeStaff = new Weapon("見習いの杖", 12);
                    player.weapons.add(apprenticeStaff);
                    result = "新しい武器「見習いの杖」を手に入れた！";
                    break;
            }
            appendColoredText(result + "\n", Color.YELLOW, 22);
        } else {
            appendColoredText("何もない部屋だ。\n", Color.BLACK);
        }
        updateMapView();

        // ★装備ラベルを更新
        weaponLabel.setText("武器: " + player.weapon.name);
        armorLabel.setText("防具: " + player.armor.name);
    }

    private void generateMap(int width, int height) {
        dungeonMap = new Room[height][width];
        Random rand = new Random();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean enemy = rand.nextInt(100) < 30;
                boolean treasure = !enemy && rand.nextInt(100) < 20;
                boolean pitfall = !enemy && !treasure && rand.nextInt(100) < 15;
                dungeonMap[y][x] = new Room(enemy, treasure, pitfall, "部屋(" + x + "," + y + ")");
            }
        }
        // スタート地点
        dungeonMap[0][0] = new Room(false, false, false, "スタート地点");
        // ボス部屋（右下）
        dungeonMap[height - 1][width - 1] = new Room(false, false, false, "ボス部屋");
        playerX = 0;
        playerY = 0;
    }

    // プレイヤーの移動
    private void movePlayer(int dx, int dy) {
        int newX = playerX + dx;
        int newY = playerY + dy;
        if (newX < 0 || newY < 0 || newX >= dungeonMap[0].length || newY >= dungeonMap.length) {
            appendColoredText("これ以上進めません。\n", Color.BLACK);
            return;
        }
        // ボス部屋に入る条件判定
        if (newX == dungeonMap[0].length - 1 && newY == dungeonMap.length - 1) {
            if (!allRoomsVisited()) {
                appendColoredText("全ての部屋を探索しないとボス部屋には入れません！\n", Color.RED, 20);
                return;
            } else {
                playerX = newX;
                playerY = newY;
                Room room = dungeonMap[playerY][playerX];
                if (!room.isVisited)
                    room.isVisited = true;
                appendColoredText("【ボス部屋に入った！】\n", Color.MAGENTA, 24);
                // ボス戦開始
                Enemy boss = new Enemy("ボス", player.level + 3);
                enemies = new ArrayList<>();
                enemies.add(boss);
                startBattle();
                updateMapView();
                return; // ★ここでreturnして通常部屋のイベント処理をスキップ
            }
        }
        playerX = newX;
        playerY = newY;
        Room room = dungeonMap[playerY][playerX];
        if (!room.isVisited) {
            room.isVisited = true;
            // 通常部屋のイベント処理（宝箱・敵・落とし穴など）
            if (room.hasEnemy) {
                appendColoredText("敵が現れた！\n", Color.BLACK, 22);
                Enemy enemy = new Enemy("モンスター", player.level);
                enemies = new ArrayList<>();
                enemies.add(enemy);
                startBattle();
            } else if (room.hasTreasure) {
                appendColoredText("【宝箱を見つけた！】\n", Color.YELLOW, 22);
                Random rand = new Random();
                int treasureType = rand.nextInt(4); // 0,1,2,3の4種類に拡張
                String result = "";
                switch (treasureType) {
                    case 0:
                        player.addItem("Health Potion");
                        result = "回復薬を手に入れた！";
                        break;
                    case 1:
                        Weapon newWeapon = new Weapon("銀の剣", 20);
                        player.weapons.add(newWeapon);
                        result = "新しい武器「銀の剣」を手に入れた！";
                        break;
                    case 2:
                        Armor newArmor = new Armor("銀の鎧", 10, 30);
                        player.armors.add(newArmor);
                        result = "新しい防具「銀の鎧」を手に入れた！";
                        break;
                    case 3:
                        Weapon apprenticeStaff = new Weapon("見習いの杖", 12);
                        player.weapons.add(apprenticeStaff);
                        result = "新しい武器「見習いの杖」を手に入れた！";
                        break;
                }
                appendColoredText(result + "\n", Color.YELLOW, 22);
            } else if (room.hasPitfall) {
                appendColoredText("落とし穴に落ちた！ダメージを受けた！\n", Color.BLUE);
                player.health -= 10;
                if (player.health < 0)
                    player.health = 0;
            } else {
                appendColoredText("何もない部屋だ。\n", Color.BLACK);
            }
        } else {
            appendColoredText("既に訪れた部屋です。\n", Color.BLACK);
        }
        updateMapView();
    }

    // 戦闘開始
    private void startBattle() {
        if (!enemies.isEmpty()) {
            Enemy currentEnemy = enemies.get(0);
            appendColoredText(player.name + "は" + currentEnemy.name + "と戦い始めました！\n", Color.RED);

            actionPanel.setVisible(true);
            setActionButtonsEnabled(true);
            setMoveButtonsEnabled(false);

            // ★敵の種類で画像を切り替え
            String imagePath;
            if ("ボス".equals(currentEnemy.name)) {
                imagePath = "fantasy_dragon.png";
            } else {
                imagePath = "fantasy_goblin.png";
            }
            try {
                ImageIcon icon = new ImageIcon(imagePath);
                Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                enemyImageLabel.setIcon(new ImageIcon(img));
                enemyImageLabel.setVisible(true);
            } catch (Exception e) {
                enemyImageLabel.setIcon(null);
                enemyImageLabel.setVisible(false);
            }
        }
    }

    // ゲームの状態を確認
    private void checkGameStatus() {
        if (!player.isAlive()) {
            appendColoredText(player.name + "は死亡しました。ゲームオーバー！\n", Color.RED);
            actionPanel.setVisible(false);
            setActionButtonsEnabled(false);
            setMoveButtonsEnabled(false);
            enemyImageLabel.setVisible(false); // ★画像非表示
        } else if (enemies.isEmpty()) {
            appendColoredText(player.name + "はすべての敵を倒しました！\n", Color.BLUE);
            actionPanel.setVisible(false);
            setActionButtonsEnabled(false);
            setMoveButtonsEnabled(true);
            enemyImageLabel.setVisible(false); // ★画像非表示
        } else {
            actionPanel.setVisible(true);
            setActionButtonsEnabled(true);
            setMoveButtonsEnabled(false); // ★戦闘中は移動禁止
        }
        updateHpBars(); // HPバーの更新
    }

    private void updateMapView() {
        for (int y = 0; y < dungeonMap.length; y++) {
            for (int x = 0; x < dungeonMap[0].length; x++) {
                Room room = dungeonMap[y][x];
                JLabel label = mapLabels[y][x];
                if (playerX == x && playerY == y) {
                    label.setText("●"); // プレイヤー
                    label.setBackground(Color.YELLOW);
                } else if (room.isVisited) {
                    label.setText("□"); // 訪れた部屋
                    label.setBackground(Color.WHITE);
                } else {
                    label.setText("■"); // 未訪問
                    label.setBackground(Color.LIGHT_GRAY);
                }
            }
        }
    }

    // HP表示を更新するメソッド
    private void updateHpBars() {
        playerHpBar.setMaximum(player.maxHealth);
        playerHpBar.setValue(player.health);
        playerHpBar.setString("プレイヤーHP: " + player.health + "/" + player.maxHealth);

        if (!enemies.isEmpty()) {
            Enemy enemy = enemies.get(0);
            enemyHpBar.setMaximum(50 + enemy.level * 10); // 敵の最大HP
            enemyHpBar.setValue(enemy.health);
            enemyHpBar.setString(enemy.name + " HP: " + enemy.health);
            enemyHpBar.setVisible(true);
        } else {
            enemyHpBar.setVisible(false);
        }
    }

    // ボタンの有効/無効を設定
    private void setActionButtonsEnabled(boolean enabled) {
        attackButton.setEnabled(enabled);
        healButton.setEnabled(enabled);
        skillButton.setEnabled(enabled);
        dialogInventoryButton.setEnabled(enabled);
    }

    // RPGGameGUIクラスのメンバーに追加
    private void setMoveButtonsEnabled(boolean enabled) {
        upButton.setEnabled(enabled);
        downButton.setEnabled(enabled);
        leftButton.setEnabled(enabled);
        rightButton.setEnabled(enabled);
    }

    // テキストを色付きでgameAreaに追加するユーティリティメソッドを追加
    private void appendColoredText(String text, Color color) {
        appendColoredText(text, color, 16); // 通常は16pt
    }

    private void appendColoredText(String text, Color color, int fontSize) {
        StyledDocument doc = gameArea.getStyledDocument();
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setForeground(attr, color);
        StyleConstants.setAlignment(attr, StyleConstants.ALIGN_CENTER);
        StyleConstants.setFontSize(attr, fontSize); // ★フォントサイズ追加
        int len = doc.getLength();
        try {
            doc.insertString(len, text, attr);
            doc.setParagraphAttributes(len, text.length(), attr, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        gameArea.setCaretPosition(doc.getLength());
    }

    private boolean allRoomsVisited() {
        for (int y = 0; y < dungeonMap.length; y++) {
            for (int x = 0; x < dungeonMap[0].length; x++) {
                // ボス部屋は除外
                if (!(x == dungeonMap[0].length - 1 && y == dungeonMap.length - 1)) {
                    if (!dungeonMap[y][x].isVisited)
                        return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new RPGGameGUI().setVisible(true);
            }
        });
    }
}
