type Props = {
  description?: string;
};

export function VideoDescription({ description }: Props) {
  if (!description) return null;
  return <div className="rounded-xl bg-accent/40 p-3 text-sm text-muted-foreground">{description}</div>;
}
