# Queue Management System

Sistema de Gerenciamento de Filas de Atendimento desenvolvido com o objetivo de estudar, praticar e demonstrar conceitos de back-end utilizando Java e Spring Boot.

O sistema simula o funcionamento de filas de atendimento comuns em bancos, clínicas e estabelecimentos comerciais, aplicando regras de negócio como prioridade, ordem de atendimento e controle de status.

---

## 🎯 Objetivo do Projeto

Este projeto tem como finalidade:

- Praticar desenvolvimento back-end com **Java e Spring Boot**
- Aplicar **regras de negócio reais**
- Trabalhar com **API REST**
- Utilizar **MySQL** como banco de dados relacional
- Desenvolver um projeto organizado e escalável para **portfólio profissional**

---

## 🛠️ Tecnologias Utilizadas

- Java
- Spring Boot
- Spring Data JPA
- MySQL
- Maven
- Git & GitHub
---

## 📌 Funcionalidades

- Criação de filas de atendimento
- Geração de senhas para clientes
- Atendimento por ordem de chegada
- Suporte a senhas prioritárias
- Controle de status das senhas:
  - AGUARDANDO
  - EM_ATENDIMENTO
  - FINALIZADO

---

## 🧠 Regras de Negócio (em desenvolvimento)

- Senhas prioritárias possuem precedência sobre senhas normais
- Não é possível atender uma fila vazia
- Uma senha finalizada não pode ser atendida novamente

---

## 🗂️ Estrutura do Projeto

O projeto segue uma arquitetura em camadas, separando responsabilidades entre:

- Controllers
- Services
- Repositories
- Entidades
- DTOs
- Exceptions

---

## 🚀 Próximos Passos

- Implementar autenticação e autorização
- Adicionar histórico de atendimentos
- Implementar testes unitários
- Criar relatórios básicos

---

## 📄 Status do Projeto

🚧 Em desenvolvimento
