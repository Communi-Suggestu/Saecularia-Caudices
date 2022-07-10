# Saecularia-Caudices
API for handling blocks which have properties backed by data stored in a block entity.

By default this system provides a cross platform api in the `IBlockWithWorldyProperties` interface.
On Forge this class is implemented automatically by its patching, while on Fabric we patch it in via mixins.

## Licence
MIT

## How to use:
Implement the `IBlockWithWorldyProperties` interface on a `Block` class or one of its inheritors.
Then Jar-In-Jar the interface where needed (Forge only needs the core package, while Fabric needs the full Fabric packages embedded).
Note using Shadow here will not work due to the mixins on fabric, and just looks weird on forge, but it could work on forge.
