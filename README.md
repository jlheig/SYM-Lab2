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


### 4.3

### 4.4

### 4.5

### 4.6