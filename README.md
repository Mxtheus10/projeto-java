# 🎉 Gerador de Eventos Históricos

Projeto desenvolvido em Java com interface Swing, padrão MVC, persistência via JDBC/MySQL e foco em cadastro e gerenciamento de eventos históricos.

## 🚀 Tecnologias utilizadas

- Java 17
- Swing (GUI)
- JDBC
- MySQL
- Maven
- Padrão MVC + DAO
- Singleton para controle de conexão

## 📦 Estrutura do Banco de Dados

Tabelas:
- `eventos`
- `pessoas`
- `evento_pessoa` (relacionamento N:N)

Script completo disponível em: [`banco.sql`](./banco.sql)

## 🧠 Funcionalidades

- Cadastro de eventos com nome, data e descrição
- Cadastro de pessoas
- Vinculação de pessoas a eventos com papel (participante, palestrante, etc)
- Interface gráfica interativa
- Validações de entrada e regras de negócio

## 🛠️ Como rodar

```bash
git clone https://github.com/seuuser/projeto-java.git
cd projeto-java
mvn clean compile
java -cp "target/classes;CAMINHO_MYSQL_CONNECTOR" com.historiaevents.view.MainApp
