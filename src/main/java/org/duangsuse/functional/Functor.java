package org.duangsuse.functional;
import java.util.function.Function;

/**
 * Morphism of categories
 * @author duangsuse
 * @implNote 辣鸡 Functor 定义... 虽然顺便囊括了一下 Monad，懒得重构
 * Actual functor definition:
 * <br>
 * <code><pre>
 * class Category (c :: * -> *) where
 *   (.) :: forall x y z. c y z -> c x y -> c x z
 *   id :: forall a. c a a
 * class (Category c, Category d) => Functor t c d where
 *   fmap :: forall a b. c a b -> d (t a) (t b)
 * type Endofunctor t c = Functor t c c
 * class Endofunctor m c => Monad m where
 *   (>>=) :: m a -> (a -> m b) -> m b 
 * </pre></code>
 * @param <T> Functor type {@code t a}
 * @param <R> Raw functor type {@code t}
 * @param <ℂ> Inner category type
 */
public interface Functor<T extends Functor<T, ? extends R, ℂ>, R, ℂ> {
  /**
   * Functor map (map category of categories)
   * @param B new type from {@code a -> b}
   * @param f mapper function (morphism)
   * @return new functor, losing its original actual type parameters
   * {@code T} to {@code ?}, all inner categories are mapped using {@code f :: a -> b}
   */
  public <B> Functor<? extends Functor<? extends R, ? extends R, B>,
		  ? extends R, B> fmap(Function<? super ℂ, B> f);

  /**
   * Create a functor of category cat
   * @param cat inner category to wrapped
   * @return Functor of {@code cat}
   */
  public Functor<T, R, ℂ> eta(ℂ cat);
  
  /**
   * Create a functor of unknown category, useful when implementing {@code fmap(...)}
   * @param cat inner category to wrapped
   * @return Functor of {@code cat}
   */
  public <B> Functor<?, R, B> etaₓ(B cat);

  /**
   * Get inner category (flatten)
   * @return inner category {@code cat}
   * @see Functor#eta(Object)
   */
  public ℂ join();

  /**
   * Monad flat map (map functor to category)
   * <br>Map using {@code (a -> t b)}, flatten result twice.
   * @return {@code Object} back to Java, no functor wrapper
   */
  public default <B> Functor<?, R, B> flatMap(Function<? super ℂ, Functor<?, R, B>> f)
  	{ return fmap(f).join(); }
}
