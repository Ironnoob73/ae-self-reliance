---
navigation:
  parent: 0_index.md
  title: Energy Interface
  icon: ae_crystal_industries:energy_interface
  position: 230
item_ids:
  - ae_crystal_industries:energy_interface
  - ae_crystal_industries:energy_interface_part
---

<Row gap="1">
    <BlockImage id="energy_interface" scale="2" />
    <ItemImage id="energy_interface_part" scale="2" />
</Row>

# Energy Interface

The **Energy Interface** is a block that converts AE within an ME network into FE.

By default, every 4AE will be converted into 1FE.

## Settings

You can set the conversion efficiency and the FE storage limit. If the storage limit is adjusted, any FE exceeding the new limit will be destroyed!

## Priority

Within a network, only the Energy Interface with the highest priority will function—until its FE storage is full or it is disabled via Redstone control.

## Upgrade

- <ItemLink id="ae2:energy_card" /> reduces the AE consumption by 1 AE per FE, down to a minimum of 1 AE/FE.

- <ItemLink id="ae2:redstone_card" /> adds redstone control.