create table menu
(
    idMenu      int auto_increment
        primary key,
    nom         varchar(100) not null,
    description text         null
);

create table plat
(
    idPlat      int auto_increment
        primary key,
    nom         varchar(100)   not null,
    description text           null,
    prix        decimal(10, 2) not null,
    idMenu      int            null,
    imagePlat   longblob       null,
    constraint plat_ibfk_1
        foreign key (idMenu) references menu (idMenu)
            on delete set null
);

create index idMenu
    on plat (idMenu);

create table utilisateur
(
    idUtilisateur  int auto_increment
        primary key,
    nomUtilisateur varchar(100)                             not null,
    motDePasse     varchar(100)                             not null,
    role           enum ('client', 'serveuse', 'cuisinier') not null,
    constraint nomUtilisateur
        unique (nomUtilisateur)
);

create table commande
(
    idCommande   int auto_increment
        primary key,
    idClient     int                                                                                             not null,
    dateCommande datetime                                                              default CURRENT_TIMESTAMP null,
    statut       enum ('en_attente', 'en_traitement', 'pretee', 'annulee', 'terminee') default 'en_attente'      null,
    montantTotal decimal(10, 2)                                                                                  null,
    constraint commande_ibfk_1
        foreign key (idClient) references utilisateur (idUtilisateur)
            on delete cascade
);

create index idClient
    on commande (idClient);

create table commandeplat
(
    idCommande int           not null,
    idPlat     int           not null,
    quantite   int default 1 null,
    primary key (idCommande, idPlat),
    constraint commandeplat_ibfk_1
        foreign key (idCommande) references commande (idCommande)
            on delete cascade,
    constraint commandeplat_ibfk_2
        foreign key (idPlat) references plat (idPlat)
            on delete cascade
);

create index idPlat
    on commandeplat (idPlat);

create table facture
(
    idFacture    int auto_increment
        primary key,
    idCommande   int                                not null,
    dateFacture  datetime default CURRENT_TIMESTAMP null,
    montantTotal decimal(10, 2)                     null,
    constraint facture_ibfk_1
        foreign key (idCommande) references commande (idCommande)
            on delete cascade
);

create index idCommande
    on facture (idCommande);


