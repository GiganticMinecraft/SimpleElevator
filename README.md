# SimpleElevator

同じワールド内で、同じ X/Z 座標に設置されたエレベーター台の間をプレイヤーが上下移動できる Paper プラグインです。

## 動作環境

- Paper API `1.18.2-R0.1-SNAPSHOT`
- Java 17

## 導入

1. [Releases](../../releases) などから `SimpleElevator` の JAR ファイルを取得します。
2. JAR ファイルを Paper サーバーの `plugins/` ディレクトリへ配置します。
3. サーバーを起動または再起動します。

## 使い方

### エレベーター台を作る

次の 2 ブロックを縦に設置するとエレベーター台になります。

```text
HEAVY_WEIGHTED_PRESSURE_PLATE
IRON_BLOCK
```

複数の台を同じワールドの同じ X/Z 座標に設置してください。移動先は近い順にワールドの高度範囲内を検索します。移動先のプレート直上には、プレイヤーが入れる空間が必要です。

### 移動する

- 上へ移動: エレベーター台の上で上方向へ移動する（飛行中を除く）
- 下へ移動: エレベーター台の上でしゃがみ始める

移動先は近い順に検索されます。移動先の直上が固体ブロック、水、溶岩、炎、クモの巣、スイートベリーの茂みなどの場合、その台はスキップされます。

移動時は X 座標、Z 座標、向き、視点を維持し、Y 座標だけが変更されます。クールダウンはありません。

## 固定値

エレベーター台は、`HEAVY_WEIGHTED_PRESSURE_PLATE` と、その直下の `IRON_BLOCK` の組み合わせです。

## 権限

| 権限ノード | 既定値 | 説明 |
| --- | --- | --- |
| `elevator.up` | `true` | 上方向への移動を許可します |
| `elevator.down` | `true` | 下方向への移動を許可します |

上移動と下移動の権限は独立しています。

## 開発

### miseを使う場合

miseをインストールしたうえで、プロジェクトのルートディレクトリから実行します。

```shell
# 初回のみ
mise install

# ビルド
mise run build

# テストのみ実行
mise run test

# YAML lint
mise run lint-yaml

# Markdown lint
mise run lint-markdown

# GitHub Actions security scan
mise run lint-gha

# Renovate設定の検証
mise run validate-renovate
```

Windows でも同じmiseタスクを使用できます。

ビルドされた JAR は `build/libs/` に出力されます。

### miseを使わない場合

JDK 17 を別途用意し、`JAVA_HOME` または `PATH` を設定してください。YAML lintにはPythonの仮想環境を使用します。

```shell
# 初回のみ
python3 -m venv .venv
. .venv/bin/activate
python -m pip install yamllint==1.37.1

# ビルド
./gradlew build

# テストのみ実行
./gradlew test

# YAML lint
python -m yamllint --format github --config-file \
  .github/.yamllint.yaml .github src/main/resources
```

Windowsでは、`py -m venv .venv`で仮想環境を作成します。
`.venv\Scripts\activate`で有効化し、Gradleには
`gradlew.bat build`または`gradlew.bat test`を使用します。

## リリース

リリースは、署名付き `vX.Y.Z` タグの push を起点に GitHub Actions が実行します。

リリース担当者は、署名鍵が設定された環境で次を実行します。
署名付き Git tag を作成するため、これらの task は Git backend が有効な checkout で実行してください。

```shell
# 署名付きタグを作成
mise add-tag 1.0.0

# 必要に応じて署名を確認
git tag -v v1.0.0

# タグを push してリリース処理を開始
mise push-tag 1.0.0
```

タグの push 後、Java 17 でのビルド・テスト、JAR の SHA256 生成、GitHub artifact attestation、
Draft Release の作成・公開を行います。公開された Release は Immutable になり、JAR とチェックサムは変更できません。

Release には次のファイルが添付されます。

- `SimpleElevator-v<version>.jar`
- `SimpleElevator-v<version>.jar.sha256`

JAR の検証には次を使用できます。

```shell
sha256sum -c SimpleElevator-v1.0.0.jar.sha256
gh attestation verify SimpleElevator-v1.0.0.jar -R GiganticMinecraft/SimpleElevator
gh release verify v1.0.0 -R GiganticMinecraft/SimpleElevator
```

## CI

Pull Request では次のチェックが実行されます。

ビルド・lint・設定検証は、開発時と同じmiseタスクを使用します。

- Java 17 での Gradle ビルド・テスト
- Markdown lint
- YAML lint（GitHub Actions と `plugin.yml`）
- GitHub Actions のセキュリティ検査
- Renovate 設定の検証

## ドキュメント

- [基本設計](docs/BASIC_DESIGN.md)
- [詳細設計](docs/DETAIL_DESIGN.md)

## ライセンス

[GPLv3 License](./LICENSE)
