Mandatory Spaces Add-on
=======

With this addon, you can configure spaces in which all users will be added.
When a user connects to the platform, a listener checks for spaces configured, and add user in all theses spaces.

To configure defaults spaces, add this property in gatein/conf/exo.properties 
exo.addons.mandatorySpaces⁼space1,space2,space3

You can set as many spaces you want. The spce name must be the technical name of the space, not the displayed name.

Since version 1.0.4, you can define excluded groups. If a user is in one of theses groups, he will not be added in mandatory list
To configure excluded groups, add this property in gatein/conf/exo.properties 
exo.addons.excludedGroups⁼/developers,/organization/executive-board

 
Install
=====

./addon install mandatory-spaces


Version
=====

1.0.2 is tested against eXo PLF 5.1.0
1.0.4 is tested against eXo PLF 5.2.x
