Mandatory Spaces Add-on
=======

With this addon, you can configure spaces in which all users will be added.
When a user connects to the platform, a listener checks for spaces configured, and add user in all theses spaces.

To configure defaults spaces, add this property in gatein/conf/exo.properties 
exo.addons.mandatorySpaces⁼space1,space2,space3

You can set as many spaces you want. The space name must be the technical name of the space, not the displayed name.


Install 
=====

./addon install mandatory-spaces