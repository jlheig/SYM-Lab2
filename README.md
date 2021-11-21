# Rapport SYM-Lab 2

## Questions

### 4.1 Traitements et Erreurs
Dans le cas où le serveur n'est pas joignable, une des méthodes serait celle appliqué dans le point 3.2 où si le serveur n'est pas joignable, les messages à envoyer s'accumulent dans une queue et toutes les 5 secondes, le client tente de renvoyer au serveur les messages en attentes.
Par contre cette méthode a comme inconvénients:
- Les messages en attente sont ssauvegardés dans l'activité, ce qui veut dire que si l'activité est détruite, alors les messages en attente aussi.
- Contrairement à de l'Active Poll traditionel, on ne tient pas compte du fait qu'on ne peut essayer de renvoyer les données que lors des fenêtres de maintenance

Dans le case où l'on reçoit un erreur HTTP dans le body, il faudrait effectuer un traitement pour chaque erreur: Par exemple si l'on a un code 3xx de redirection, il faudrait du coup refaire une requête mais sur l'url proposé par la réponse du serveur

Dans ces 2 cas, il faudrait rajouter une méthode `handleError` dans le `CommunicationEventListener`, ainsi on préciserait quoi faire en cas d'erreur:
```kotlin
interface CommunicationEventListener {
    fun handleServerResponse(response : String)
    fun handleError()
}
```

Et l'on adapterait le code de `sendRequest` de `SymComManager` comme ceci:

```kotlin
try {

    /**
    *   Connexion et envoi au serveur omis pour raisons de lisibilité
    **/

    //Réception réussie
    handler.post{
        l.handleServerResponse(text)
    }
}
catch(e: Exception){
    //Erreur durant la connexion
    handler.post{
        l.handleError()
    }   
}
```

### 4.2 Authentification
Une transmission asynchrone pourrait être utilisé pour de l'authentification. La limitation serait que l'on ne pourrait pas continuer le processus d'authentification avant d'avoir reçu la réponse du serveur.
Une transmission différée serait plus complexe car, l'app ne pourrait pas continuer de tourner normalement si ce dernier veut accéder à des fonctionalités qui requiert une authentification

### 4.3 Threads concurrents

Les 2 threads pourraient tenter d'agir sur les mêmes données et donc fausser leur valeur et le processus entier. Il est également probable que cela demande plus de ressource et provoquent des ralentissements.

### 4.4 Ecriture différée

#### une connexion par transmission différée 
Les avantages d'une connexion par transmission différée est qu'il simplifie le traitement des requêtes pour le serveur. L'inconvénient est que cela prendra plus de temps au serveurs pour traiter toutes les requêtes.

#### Multiplexage
Le grand avantage du multiplexage est qu'on peut envoyer toutes les données lors d'une seule connexion prenant ainsi beaucoup moins de temps à traiter. Par contre, il faut améliorer le protocole de communication entre le serveur et le client, pour que le client précise aux serveurs qu'il va recevoir plusieurs requêtes et le début et la fin de chaque requêtes. De plus comme on doit donner plus d'informations au serveur, les données prennent plus de place.

### 4.5 Transmission d'objets

A) SOAP offre un système de gestion d'erreurs inclus tandis que REST n'en offre pas. Cela ne nous permet donc pas de vérifier si les données qui se trouvent dans la requête sont valides, on peut donc imaginer envoyer des données invalides à notre service. Pour pallier à cela, on est obligé d'instaurer un système de validation personnel.

REST par contre est considéré comme plus rapide et a été pensé pour une utilisation dans le Web. Les API REST sont également considérées comme plus légères et donc plus adapté à certains domaines comme l'IoT. REST est également plus simple à implémenter dans n'importe quelle application puisqu'il n'y a aucun système de validation prédéfini.

B) Les transactions s'alourdissent à chaque auteur, ce qui fait que dans le cas où on possèderait un nombre d'auteurs très grand, les requêtes deviendraient très lourde, cela demanderait beaucoup de performance et de temps.

Afin de pallier à ce problème, on pourrait imaginer une liste de paramètres permettant de filtrer le nombre d'auteurs que l'on souhaite récupérer, par exemple : 

* le nombre d'auteurs que l'on veut récupérer
* le nombre de livres écrit par chaque auteur
* La lettre par laquelle commence leur nom ou le nom de leur oeuvre

Cela permettrait de réduire la taille de la requête et donc de pallier à ce problème.

### 4.6 Transmission compressée


| type of file   | no deflate file size | deflate file size | Gain  |
| -------------- | -------------------- | ----------------- | ----- |
| Hello          | 5 B                  | 7 B               | - 40% |
| long text      | 490 B                | 286 B             | 42%   |
| json           | 147 B                | 101 B             | 32%   |
| xml            | 296 B                | 187 B             | 37 %  |

Plus les textes sont long, plus le gain de la compression est important. Par contre pour de tous petits textes, la compression est inutiles voire agrandit la taille du fichier.
 
PLus les textes sont long, plus il faudra du temps aux machines pour décompresser du texte afin de le traiter.