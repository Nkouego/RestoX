# 🍽️ Resto X

**Resto X** est une application de gestion interne de restaurant visant à digitaliser les opérations quotidiennes traditionnellement manuelles (prise de commande, paiements, gestion du personnel, etc.).

Elle permet aux restaurateurs d’optimiser leur organisation, réduire les erreurs humaines et améliorer l’efficacité du service.

---

## 🎯 Objectif

L’objectif principal de **Resto X** est de moderniser la gestion des restaurants en :

- Remplaçant les prises de commande papier  
- Réduisant la dépendance aux paiements en cash  
- Limitant les déplacements inutiles des serveurs  
- Réduisant les erreurs de commande  
- Diminuant le temps d’attente des clients  

---

## 👥 Cible

- Restaurateurs  
- Équipes de restauration  

---

## 🚀 Fonctionnalités

### Authentification (terminée)

- Inscription (Admin)
- Connexion
- Vérification d’email
- Mot de passe oublié
- Réinitialisation de mot de passe
- Rafraîchissement de token
- Déconnexion
- Renvoi de code

---

### Gestion des restaurants

- Création de restaurant
- CRUD complet
- Isolation des données (multi-tenant)

---

###  Gestion du staff

- Invitation des membres
- CRUD du personnel
- Utilisateurs multi-restaurants
- Rôles dynamiques

---

## Rôles

- SUPER_ADMIN  
- ADMIN  
- MANAGER  
- CASHIER  
- SERVER  
- WAITER  

---

## 🛠️ Stack technique

- Java / Spring Boot  
- Spring Security  
- OAuth2 Resource Server  
- Spring Data JPA  
- Spring Mail  
- Lombok  
- MapStruct  

---

## Architecture

```bash
src/
│── entity/
│── repository/
│── service/
│── controller/
│── dto/
│── mapper/
│── config/
│── common/
```

##  État du projet

En cours de développement

---

## 📄 Licence

MIT

---

## 📧 Contact

Nkouego Larissa 
larissankouego@gmail.com 
