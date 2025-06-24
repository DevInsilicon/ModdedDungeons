# Entity Management System

The Entity Management System provides a powerful and extensible framework for creating custom entities with advanced item and armor interactions. It mirrors the architecture of the existing Item Management system for consistency and ease of use.

## Key Features

- **Base Entity Classes**: Extensible base classes with useful events and methods
- **Custom Item Integration**: Entities can use custom items with specialized behaviors
- **Armor Management**: Full armor system with damage absorption and effects
- **Event-Driven Architecture**: Clean, extensible event system for custom behaviors
- **Registry System**: Centralized management of custom entity types

## Core Classes

### BaseEntity
The foundation class for all custom entities. Provides:
- Attack and damage event handling
- Spawn and death event handling
- Item interaction capabilities
- Armor management integration

### BaseEntityArmor
Extends BaseEntity with armor-specific functionality:
- Damage absorption calculations
- Armor equipment events
- Armor-specific attack behaviors

### EntityManager
Central management system that:
- Handles all entity events
- Manages entity registry
- Integrates with ItemManager for weapon/armor interactions
- Provides entity spawning utilities

## Creating Custom Entities

### Basic Entity Example

```java
public class CustomBeast extends BaseEntity {
    public CustomBeast() {
        super("custom_beast", 
              Component.text("Custom Beast"), 
              List.of("A fierce custom beast"),
              EntityType.WOLF,
              EntityManager.entityKey,
              5, // Level requirement
              false, // Can spawn outside dungeon
              25.0, // Max health
              7.0); // Base damage
    }
    
    @Override
    public double entityAttacked(LivingEntity attacker, Entity victim, EntityDamageByEntityEvent event) {
        // Custom attack behavior - 20% damage boost
        return event.getDamage() * 1.2;
    }
    
    @Override
    public void entitySpawn(LivingEntity entity) {
        // Custom spawn behavior
        entity.setCustomName(getDisplayName());
        entity.setCustomNameVisible(true);
        // Add particle effects, sounds, etc.
    }
}
```

### Armored Entity Example

```java
public class CustomWarrior extends BaseEntityArmor {
    public CustomWarrior() {
        super("custom_warrior",
              Component.text("Custom Warrior"),
              List.of("A heavily armored warrior"),
              EntityType.ZOMBIE,
              EntityManager.entityKey,
              10, // Level requirement
              false, // Can spawn outside dungeon
              40.0, // Max health
              12.0, // Base damage
              ArmorType.CHESTPLATE, // Preferred armor
              20.0, // Damage absorption %
              8.0, // Max HP absorption
              3.0); // Flat damage reduction
    }
    
    @Override
    public void entityArmorAttacked(LivingEntity attacker, Entity victim, EntityDamageByEntityEvent event) {
        // Special armor attack behavior
        if (Math.random() < 0.15) { // 15% chance
            // Knockback effect
            if (victim instanceof LivingEntity) {
                ((LivingEntity) victim).setVelocity(
                    attacker.getLocation().getDirection().multiply(0.8)
                );
            }
        }
    }
}
```

## Custom Item Integration

Entities can use custom items through the new BaseItem methods:

### Entity-Specific Item Behavior

```java
public class CustomSword extends BaseItem {
    // ... constructor ...
    
    @Override
    public double entityAttackedWhileHeld(LivingEntity attacker, Entity victim, EntityDamageByEntityEvent event) {
        // Different behavior when entities use this sword
        if (attacker instanceof Zombie) {
            // Zombies get 50% more damage with this sword
            return event.getDamage() * 1.5;
        }
        return event.getDamage();
    }
    
    @Override
    public void entityInteraction(LivingEntity entity, Block block, ItemStack item) {
        // Custom entity interaction with this item
        // Could trigger special abilities, effects, etc.
    }
}
```

## Registration

Add your custom entities to the EntityManager registry:

```java
// In EntityManager.registry() method
public void registry() {
    EntityTypes.clear();
    PrecompiledArmorEntityTypes.clear();
    
    // Add your custom entities
    EntityTypes.add(new CustomBeast());
    EntityTypes.add(new CustomWarrior());
    
    // Precompile armor entities
    for (BaseEntity entity : EntityTypes) {
        if (entity instanceof BaseEntityArmor) {
            PrecompiledArmorEntityTypes.add((BaseEntityArmor) entity);
        }
    }
}
```

## Spawning Entities

```java
// Get entity type
BaseEntity warriorType = EntityManager.getEntityType("custom_warrior");

// Spawn entity at location
LivingEntity warrior = EntityManager.spawnEntity(warriorType, playerLocation);

// Entity will automatically have all custom behaviors applied
```

## Event Flow

1. **Entity Events**: All entity damage, attack, spawn, and death events are captured
2. **Entity Processing**: Custom entity behaviors are applied
3. **Item Integration**: Weapon and armor interactions are processed
4. **Final Application**: All modifications are applied to the event

This system provides plugin authors with complete control over entity behavior while maintaining clean integration with the existing item management system.